package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.base.AbstractMarshaller;

public class CharacterMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return char.class == type || Character.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		if (Character.class == type) {
			if (!writePossibleNull(value, dataOutput)) {
				return;
			}
		}

		dataOutput.writeChar((Character) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
		if (Character.class == type) {
			if (isNull(dataInput)) {
				return null;
			}
		}

		return (V) Character.valueOf(dataInput.readChar());
	}
}
