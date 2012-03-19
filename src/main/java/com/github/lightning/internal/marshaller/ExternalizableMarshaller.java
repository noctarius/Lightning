package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.github.lightning.base.AbstractMarshaller;

public class ExternalizableMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return Externalizable.class.isAssignableFrom(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		((Externalizable) value).writeExternal((ObjectOutput) dataOutput);
	}

	@Override
	public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
		try {
			((Externalizable) value).readExternal((ObjectInput) dataInput);
			return value;
		}
		catch (ClassNotFoundException e) {
			throw new IOException("Error while deserialization", e);
		}
	}
}
