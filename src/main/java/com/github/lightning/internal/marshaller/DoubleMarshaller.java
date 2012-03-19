package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.base.AbstractMarshaller;

public class DoubleMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return double.class == type || Double.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		if (Double.class == type) {
			if (!writePossibleNull(value, dataOutput)) {
				return;
			}
		}

		dataOutput.writeDouble((Double) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
		if (Double.class == type) {
			if (isNull(dataInput)) {
				return null;
			}
		}

		return (V) Double.valueOf(dataInput.readDouble());
	}
}
