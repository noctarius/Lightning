package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.base.AbstractMarshaller;

public class LongMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return long.class == type || Long.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		if (Long.class == type) {
			if (!writePossibleNull(value, dataOutput)) {
				return;
			}
		}

		dataOutput.writeLong((Long) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
		if (Long.class == type) {
			if (isNull(dataInput)) {
				return null;
			}
		}

		return (V) Long.valueOf(dataInput.readLong());
	}
}
