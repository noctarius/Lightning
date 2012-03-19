package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.github.lightning.base.AbstractMarshaller;

public class SerializableMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return Serializable.class.isAssignableFrom(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream((OutputStream) dataOutput);
		stream.writeObject(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
		ObjectInputStream stream = new ObjectInputStream((InputStream) dataInput);
		try {
			return (V) stream.readObject();
		}
		catch (ClassNotFoundException e) {
			throw new IOException("Error while deserialization", e);
		}
	}
}
