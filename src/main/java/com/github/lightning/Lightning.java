package com.github.lightning;

import java.util.Arrays;

import com.github.lightning.internal.InternalSerializerCreator;

public final class Lightning {

	private Lightning() {
	}

	public static final Serializer createSerializer(SerializerDefinition... serializerDefinitions) {
		return createSerializer(Arrays.asList(serializerDefinitions));
	}

	public static final Serializer createSerializer(Iterable<? extends SerializerDefinition> serializerDefinitions) {
		return new InternalSerializerCreator().addSerializerDefinitions(serializerDefinitions).build();
	}

}
