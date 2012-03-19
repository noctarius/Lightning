package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.base.AbstractMarshaller;

public class BooleanMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return boolean.class == type || Boolean.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		if (Boolean.class == type) {
			if (!writePossibleNull(value, dataOutput)) {
				return;
			}
		}

		dataOutput.writeBoolean((Boolean) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
		if (Boolean.class == type) {
			if (isNull(dataInput)) {
				return null;
			}
		}

		return (V) Boolean.valueOf(dataInput.readBoolean());
	}
}
