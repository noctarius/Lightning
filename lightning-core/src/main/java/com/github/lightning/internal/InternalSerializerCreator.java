/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lightning.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.github.lightning.Attribute;
import com.github.lightning.ClassComparisonStrategy;
import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDescriptor;
import com.github.lightning.DefinitionBuildingContext;
import com.github.lightning.DefinitionVisitor;
import com.github.lightning.Marshaller;
import com.github.lightning.MarshallerStrategy;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.PropertyDescriptorFactory;
import com.github.lightning.SerializationStrategy;
import com.github.lightning.Serializer;
import com.github.lightning.SerializerDefinition;
import com.github.lightning.internal.beans.InternalPropertyDescriptorFactory;
import com.github.lightning.internal.instantiator.ObjenesisSerializer;
import com.github.lightning.logging.Logger;
import com.github.lightning.logging.LoggerAdapter;

public final class InternalSerializerCreator {

	private final Map<Class<?>, InternalClassDescriptor> classDescriptors = new HashMap<Class<?>, InternalClassDescriptor>();
	private final List<SerializerDefinition> serializerDefinitions = new ArrayList<SerializerDefinition>();
	private final Map<Class<?>, Marshaller> marshallers = new HashMap<Class<?>, Marshaller>();
	private final ObjenesisSerializer objenesisSerializer = new ObjenesisSerializer(true);

	private SerializationStrategy serializationStrategy = SerializationStrategy.SpeedOptimized;
	private Class<? extends Annotation> attributeAnnotation = Attribute.class;
	private ClassComparisonStrategy classComparisonStrategy = ClassComparisonStrategy.LightningChecksum;
	private Logger logger = new LoggerAdapter();

	public InternalSerializerCreator() {
	}

	public InternalSerializerCreator addSerializerDefinitions(Iterable<? extends SerializerDefinition> serializerDefinitions) {
		for (SerializerDefinition serializerDefinition : serializerDefinitions) {
			this.serializerDefinitions.add(serializerDefinition);
		}

		return this;
	}

	public InternalSerializerCreator setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public InternalSerializerCreator setAttributeAnnotation(Class<? extends Annotation> attributeAnnotation) {
		this.attributeAnnotation = attributeAnnotation;
		return this;
	}

	public InternalSerializerCreator setSerializationStrategy(SerializationStrategy serializationStrategy) {
		this.serializationStrategy = serializationStrategy;
		return this;
	}

	public InternalSerializerCreator setClassComparisonStrategy(ClassComparisonStrategy classComparisonStrategy) {
		this.classComparisonStrategy = classComparisonStrategy;
		return this;
	}

	public Serializer build() {
		PropertyDescriptorFactory propertyDescriptorFactory = new InternalPropertyDescriptorFactory(logger);
		MarshallerStrategy marshallerStrategy = new InternalMarshallerStrategy();
		DefinitionBuildingContext definitionBuildingContext = new InternalDefinitionBuildingContext(marshallerStrategy, propertyDescriptorFactory);
		DefinitionVisitor definitionVisitor = new InternalDefinitionVisitor();
		for (SerializerDefinition serializerDefinition : serializerDefinitions) {
			serializerDefinition.configure(definitionBuildingContext, objenesisSerializer);
			serializerDefinition.acceptVisitor(definitionVisitor);
		}

		List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
		for (InternalClassDescriptor classDescriptor : classDescriptors.values()) {
			classDefinitions.add(classDescriptor.build().getClassDefinition());
		}

		Map<Class<?>, ClassDescriptor> cleanedClassDescriptors = new HashMap<Class<?>, ClassDescriptor>(classDescriptors.size());
		for (Entry<Class<?>, InternalClassDescriptor> entry : classDescriptors.entrySet()) {
			cleanedClassDescriptors.put(entry.getKey(), entry.getValue());
		}

		return new InternalSerializer(new InternalClassDefinitionContainer(classDefinitions),
				classComparisonStrategy, cleanedClassDescriptors, marshallers, objenesisSerializer, logger);
	}

	private InternalClassDescriptor findClassDescriptor(Class<?> type) {
		InternalClassDescriptor classDescriptor = classDescriptors.get(type);
		if (classDescriptor == null) {
			classDescriptor = new InternalClassDescriptor(type, logger);
			classDescriptors.put(type, classDescriptor);
		}

		return classDescriptor;
	}

	private class InternalDefinitionVisitor implements DefinitionVisitor {

		private final Stack<Class<? extends Annotation>> attributeAnnotation = new Stack<Class<? extends Annotation>>();

		@Override
		public void visitSerializerDefinition(SerializerDefinition serializerDefinition) {
			// If at top level definition just add the base annotation
			if (attributeAnnotation.size() == 0) {
				if (InternalSerializerCreator.this.attributeAnnotation == null) {
					attributeAnnotation.push(Attribute.class);
				}
				else {
					attributeAnnotation.push(InternalSerializerCreator.this.attributeAnnotation);
				}
			}
			else {
				Class<? extends Annotation> annotation = attributeAnnotation.peek();
				attributeAnnotation.push(annotation);
			}
		}

		@Override
		public void visitAttributeAnnotation(Class<? extends Annotation> attributeAnnotation) {
			// Remove last element and replace it with the real annotation to
			// use right from that moment
			this.attributeAnnotation.pop();
			this.attributeAnnotation.push(attributeAnnotation);
		}

		@Override
		public void visitClassDefine(Class<?> type, Marshaller marshaller) {
			InternalClassDescriptor classDescriptor = findClassDescriptor(type);
			classDescriptor.setMarshaller(marshaller);

			marshallers.put(type, marshaller);
		}

		@Override
		public void visitAnnotatedAttribute(PropertyDescriptor propertyDescriptor, Marshaller marshaller) {
			InternalClassDescriptor classDescriptor = findClassDescriptor(propertyDescriptor.getDeclaringClass());

			if (logger.isTraceEnabled()) {
				logger.trace("Found property " + propertyDescriptor.getName() + " (" + propertyDescriptor.getInternalSignature()
						+ ") on type " + propertyDescriptor.getDeclaringClass().getCanonicalName());
			}

			classDescriptor.push(propertyDescriptor);
		}

		@Override
		public void visitPropertyDescriptor(PropertyDescriptor propertyDescriptor, Marshaller marshaller) {
			InternalClassDescriptor classDescriptor = findClassDescriptor(propertyDescriptor.getDeclaringClass());

			if (logger.isTraceEnabled()) {
				logger.trace("Found property " + propertyDescriptor.getName() + " (" + propertyDescriptor.getInternalSignature()
						+ ") on type " + propertyDescriptor.getDeclaringClass().getCanonicalName());
			}

			classDescriptor.push(propertyDescriptor);
		}

		@Override
		public void visitFinalizeSerializerDefinition(SerializerDefinition serializerDefinition) {
			// Clean this level up
			this.attributeAnnotation.pop();
		}
	}
}
