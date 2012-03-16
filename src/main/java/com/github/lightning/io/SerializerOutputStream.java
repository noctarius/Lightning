package com.github.lightning.io;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;

import com.github.lightning.Serializer;

public class SerializerOutputStream extends DataOutputStream {

	private final Serializer serializer;

	public SerializerOutputStream(OutputStream out, Serializer serializer) {
		super(out);
		this.serializer = serializer;
	}

	public void writeObject(Object object) {
		serializer.serialize(object, (DataOutput) this);
	}
}
