package com.github.lightning.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.lightning.Attribute;
import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.SerializationStrategy;
import com.github.lightning.Serializer;
import com.github.lightning.SerializerDefinition;
import com.github.lightning.logging.Logger;
import com.github.lightning.logging.LoggerAdapter;

public final class InternalSerializerCreator {

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
		// TODO implementation missing
		ClassDefinitionContainer classDefinitionContainer = new InternalClassDefinitionContainer(Collections.<ClassDefinition> emptyList());
		return new InternalSerializer(classDefinitionContainer);
	}

}
