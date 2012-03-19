package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.base.AbstractMarshaller;

public class ByteMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return byte.class == type || Byte.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		if (Byte.class == type) {
			if (!writePossibleNull(value, dataOutput)) {
				return;
			}
		}

		dataOutput.writeByte((Byte) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
		if (Byte.class == type) {
			if (isNull(dataInput)) {
				return null;
			}
		}

		return (V) Byte.valueOf(dataInput.readByte());
	}
}
