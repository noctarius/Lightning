/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directmemory.lightning.maven;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.MarshallerStrategy;
import org.apache.directmemory.lightning.SerializationStrategy;
import org.apache.directmemory.lightning.configuration.SerializerDefinition;
import org.apache.directmemory.lightning.generator.DefinitionBuildingContext;
import org.apache.directmemory.lightning.generator.DefinitionVisitor;
import org.apache.directmemory.lightning.generator.PropertyDescriptorFactory;
import org.apache.directmemory.lightning.internal.InternalClassDescriptor;
import org.apache.directmemory.lightning.internal.InternalDefinitionBuildingContext;
import org.apache.directmemory.lightning.internal.InternalMarshallerStrategy;
import org.apache.directmemory.lightning.internal.beans.InternalPropertyDescriptorFactory;
import org.apache.directmemory.lightning.internal.util.ClassUtil;
import org.apache.directmemory.lightning.internal.util.TypeUtil;
import org.apache.directmemory.lightning.logging.Logger;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.apache.directmemory.lightning.metadata.ClassDefinition;
import org.apache.directmemory.lightning.metadata.ClassDescriptor;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;


public class SerializerDefinitionAnalyser {

	private final Logger logger;

	private final Map<Class<?>, InternalClassDescriptor> classDescriptors = new HashMap<Class<?>, InternalClassDescriptor>();
	private final List<SerializerDefinition> serializerDefinitions = new ArrayList<SerializerDefinition>();
	private final Map<Class<?>, Marshaller> marshallers = new HashMap<Class<?>, Marshaller>();
	private Class<? extends Annotation> attributeAnnotation = Placeholder.class;

	private final DefinitionVisitor definitionVisitor = new GeneratorDefinitionVisitor();

	public SerializerDefinitionAnalyser(Logger logger) {
		this.logger = logger;
	}

	public void analyse(SerializerDefinition serializerDefinition) {
		PropertyDescriptorFactory propertyDescriptorFactory = new InternalPropertyDescriptorFactory(logger);
		MarshallerStrategy marshallerStrategy = new InternalMarshallerStrategy();
		DefinitionBuildingContext definitionBuildingContext = new InternalDefinitionBuildingContext(marshallerStrategy, propertyDescriptorFactory);

		serializerDefinition.configure(definitionBuildingContext, null);
		serializerDefinition.acceptVisitor(definitionVisitor);
	}

	public List<File> build(File outputFolder, SerializationStrategy serializationStrategy, String encoding) {
		Charset charset = Charset.forName(encoding);

		List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
		for (InternalClassDescriptor classDescriptor : classDescriptors.values()) {
			classDefinitions.add(classDescriptor.build(ClassUtil.CLASS_DESCRIPTORS).getClassDefinition());
		}

		List<File> files = new ArrayList<File>();
		for (ClassDescriptor classDescriptor : classDescriptors.values()) {
			if (classDescriptor instanceof InternalClassDescriptor && classDescriptor.getMarshaller() == null) {
				try {
					SourceMarshallerGenerator generator = new SourceMarshallerGenerator(charset, logger);
					File sourceFile = generator.generateMarshaller(classDescriptor.getType(), classDescriptor.getPropertyDescriptors(), serializationStrategy,
							outputFolder);

					files.add(sourceFile);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return files;
	}

	public List<SerializerDefinition> getVisitedSerializerDefinitions() {
		return serializerDefinitions;
	}

	private InternalClassDescriptor findClassDescriptor(Class<?> type) {
		InternalClassDescriptor classDescriptor = classDescriptors.get(type);
		if (classDescriptor == null) {
			classDescriptor = new InternalClassDescriptor(type, logger);
			classDescriptors.put(type, classDescriptor);
		}

		return classDescriptor;
	}

	private class GeneratorDefinitionVisitor implements DefinitionVisitor {

		private final Stack<Class<? extends Annotation>> attributeAnnotation = new Stack<Class<? extends Annotation>>();

		@Override
		public void visitSerializerDefinition(SerializerDefinition serializerDefinition) {
			// If at top level definition just add the base annotation
			if (attributeAnnotation.size() == 0) {
				if (SerializerDefinitionAnalyser.this.attributeAnnotation == null) {
					attributeAnnotation.push(Attribute.class);
				}
				else {
					attributeAnnotation.push(SerializerDefinitionAnalyser.this.attributeAnnotation);
				}
			}
			else {
				Class<? extends Annotation> annotation = attributeAnnotation.peek();
				attributeAnnotation.push(annotation);
			}

			// Save visited SerializerDefinition
			serializerDefinitions.add(serializerDefinition);
		}

		@Override
		public void visitAttributeAnnotation(Class<? extends Annotation> attributeAnnotation) {
			// Remove last element and replace it with the real annotation to
			// use right from that moment
			this.attributeAnnotation.pop();
			this.attributeAnnotation.push(attributeAnnotation);
		}

		@Override
		public void visitClassDefine(Type type, Marshaller marshaller) {
			Class<?> rawType = TypeUtil.getBaseType(type);
			InternalClassDescriptor classDescriptor = findClassDescriptor(rawType);
			classDescriptor.setMarshaller(marshaller);

			marshallers.put(rawType, marshaller);
		}

		@Override
		public void visitAnnotatedAttribute(PropertyDescriptor propertyDescriptor, Marshaller marshaller) {
			InternalClassDescriptor classDescriptor = findClassDescriptor(propertyDescriptor.getDefinedClass());

			if (logger.isTraceEnabled()) {
				logger.trace("Found property " + propertyDescriptor.getName() + " (" + propertyDescriptor.getInternalSignature() + ") on type "
						+ propertyDescriptor.getDefinedClass().getCanonicalName());
			}

			classDescriptor.push(propertyDescriptor);
		}

		@Override
		public void visitPropertyDescriptor(PropertyDescriptor propertyDescriptor, Marshaller marshaller) {
			InternalClassDescriptor classDescriptor = findClassDescriptor(propertyDescriptor.getDefinedClass());

			if (logger.isTraceEnabled()) {
				logger.trace("Found property " + propertyDescriptor.getName() + " (" + propertyDescriptor.getInternalSignature() + ") on type "
						+ propertyDescriptor.getDefinedClass().getCanonicalName());
			}

			classDescriptor.push(propertyDescriptor);
		}

		@Override
		public void visitFinalizeSerializerDefinition(SerializerDefinition serializerDefinition) {
			// Clean this level up
			this.attributeAnnotation.pop();
		}
	}

	public static @interface Placeholder {
	}
}
