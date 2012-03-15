package com.github.lightning.internal;

import java.util.ArrayList;
import java.util.List;

import com.github.lightning.Serializer;
import com.github.lightning.SerializerDefinition;

public final class InternalSerializerCreator {

	private final List<SerializerDefinition> serializerDefinitions = new ArrayList<SerializerDefinition>();

	public InternalSerializerCreator() {
	}

	public InternalSerializerCreator addSerializerDefinitions(Iterable<? extends SerializerDefinition> serializerDefinitions) {
		for (SerializerDefinition serializerDefinition : serializerDefinitions) {
			this.serializerDefinitions.add(serializerDefinition);
		}

		return this;
	}

	public Serializer build() {
		// TODO implementation missing
		return null;
	}

}
