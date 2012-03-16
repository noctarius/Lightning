package com.github.lightning.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import com.github.lightning.Serializer;

public class SerializerInputStream extends DataInputStream {

	private final Serializer serializer;

	public SerializerInputStream(InputStream in, Serializer serializer) {
		super(in);
		this.serializer = serializer;
	}

	public <T> T readObject() {
		return serializer.deserialize((DataInput) this);
	}
}
