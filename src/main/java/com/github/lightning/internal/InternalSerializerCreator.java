package com.github.lightning.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.lightning.Attribute;
import com.github.lightning.ClassDefinition;
import com.github.lightning.DefinitionVisitor;
import com.github.lightning.Marshaller;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.SerializationStrategy;
import com.github.lightning.Serializer;
import com.github.lightning.SerializerDefinition;
import com.github.lightning.logging.Logger;
import com.github.lightning.logging.LoggerAdapter;

public final class InternalSerializerCreator {

	private final Map<Class<?>, InternalClassDescriptor> classDescriptors = new HashMap<Class<?>, InternalClassDescriptor>();
	private final List<SerializerDefinition> serializerDefinitions = new ArrayList<SerializerDefinition>();

	private SerializationStrategy serializationStrategy = SerializationStrategy.SpeedOptimized;
	private Class<? extends Annotation> attributeAnnotation = Attribute.class;
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

	public Serializer build() {
		DefinitionVisitor definitionVisitor = new InternalDefinitionVisitor();
		for (SerializerDefinition serializerDefinition : serializerDefinitions) {
			serializerDefinition.acceptVisitor(definitionVisitor);
		}

		List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
		for (InternalClassDescriptor classDescriptor : classDescriptors.values()) {
			classDefinitions.add(classDescriptor.getClassDefinition());
		}

		return new InternalSerializer(new InternalClassDefinitionContainer(classDefinitions), logger);
	}

	private class InternalDefinitionVisitor implements DefinitionVisitor {

		private final Stack<Class<? extends Annotation>> attributeAnnotation = new Stack<Class<? extends Annotation>>();
		private final Stack<InternalClassDescriptor> classDescriptor = new Stack<InternalClassDescriptor>();

		@Override
		public void visitSerializerDefinition(SerializerDefinition serializerDefinition) {
			// If at top level definition just add the base annotation
			if (attributeAnnotation.size() == 0) {
				attributeAnnotation.push(InternalSerializerCreator.this.attributeAnnotation);
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
		public void visitClassDefine(Class<?> type, Marshaller<?> marshaller) {
			InternalClassDescriptor classDescriptor = findClassDescriptor(type);
			classDescriptor.setMarshaller(marshaller);
		}

		@Override
		public void visitPropertyDescriptor(PropertyDescriptor propertyDescriptor, Marshaller<?> marshaller) {
			InternalClassDescriptor classDescriptor = findClassDescriptor(propertyDescriptor.getDeclaringClass());
			classDescriptor.push(propertyDescriptor);
		}

		@Override
		public void visitFinalizeSerializerDefinition(SerializerDefinition serializerDefinition) {
			// Clean this level up and finalize building of ClassDescriptor
			this.attributeAnnotation.pop();
			InternalClassDescriptor classDescriptor = this.classDescriptor.pop();
			classDescriptor.build();
		}

		private InternalClassDescriptor findClassDescriptor(Class<?> type) {
			InternalClassDescriptor classDescriptor = classDescriptors.get(type);
			if (classDescriptor == null) {
				classDescriptor = new InternalClassDescriptor(type);
				classDescriptors.put(type, classDescriptor);
			}

			return classDescriptor;
		}
	}
}
