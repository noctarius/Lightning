package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.base.AbstractMarshaller;

public class IntegerMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return short.class == type || Integer.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		if (Integer.class == type) {
			if (!writePossibleNull(value, dataOutput)) {
				return;
			}
		}

		dataOutput.writeInt((Integer) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
		if (Integer.class == type) {
			if (isNull(dataInput)) {
				return null;
			}
		}

		return (V) Integer.valueOf(dataInput.readInt());
	}
}
