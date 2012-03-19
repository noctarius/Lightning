package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.base.AbstractMarshaller;

public class ShortMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return short.class == type || Short.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		if (Short.class == type) {
			if (!writePossibleNull(value, dataOutput)) {
				return;
			}
		}

		dataOutput.writeShort((Short) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
		if (Short.class == type) {
			if (isNull(dataInput)) {
				return null;
			}
		}

		return (V) Short.valueOf(dataInput.readShort());
	}
}
