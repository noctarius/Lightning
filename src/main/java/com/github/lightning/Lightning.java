package com.github.lightning;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.lightning.internal.InternalSerializerCreator;
import com.github.lightning.logging.Logger;
import com.github.lightning.logging.LoggerAdapter;

public final class Lightning {

	private Lightning() {
	}

	public static final Builder newBuilder() {
		return new Builder();
	}

	public static final Serializer createSerializer(SerializerDefinition... serializerDefinitions) {
		return createSerializer(Arrays.asList(serializerDefinitions));
	}

	public static final Serializer createSerializer(Iterable<? extends SerializerDefinition> serializerDefinitions) {
		return new Builder().serializerDefinitions(serializerDefinitions).build();
	}

	public static class Builder {

		private Set<SerializerDefinition> serializerDefinitions = new HashSet<SerializerDefinition>();
		private SerializationStrategy serializationStrategy = SerializationStrategy.SpeedOptimized;
		private Class<? extends Annotation> attributeAnnotation = Attribute.class;
		private Logger logger = new LoggerAdapter();

		private Builder() {
		}

		public Builder describesAttributs(Class<? extends Annotation> attributeAnnotation) {
			this.attributeAnnotation = attributeAnnotation;
			return this;
		}

		public Builder serializationStrategy(SerializationStrategy serializationStrategy) {
			this.serializationStrategy = serializationStrategy;
			return this;
		}

		public Builder serializerDefinitions(SerializerDefinition... serializerDefinitions) {
			return serializerDefinitions(Arrays.asList(serializerDefinitions));
		}

		public Builder serializerDefinitions(Iterable<? extends SerializerDefinition> serializerDefinitions) {
			for (SerializerDefinition serializerDefinition : serializerDefinitions) {
				this.serializerDefinitions.add(serializerDefinition);
			}
			return this;
		}

		public Builder logger(Logger logger) {
			this.logger = logger;
			return this;
		}

		public Serializer build() {
			return new InternalSerializerCreator().setLogger(logger).setSerializationStrategy(serializationStrategy).setAttributeAnnotation(attributeAnnotation)
					.addSerializerDefinitions(serializerDefinitions).build();
		}
	}

}
