package com.github.lightning.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.Marshaller;
import com.github.lightning.ObjectInstantiator;

public abstract class AbstractMarshaller implements Marshaller {

	@Override
	public <V> V unmarshall(Class<?> type, ObjectInstantiator objectInstantiator, DataInput dataInput) throws IOException {
		return unmarshall(type, dataInput);
	}

	protected boolean writePossibleNull(Object value, DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(value == null ? 1 : 0);
		return value != null;
	}

	protected boolean isNull(DataInput dataInput) throws IOException {
		byte isNull = dataInput.readByte();
		return isNull == 1 ? true : false;
	}

	protected abstract <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException;
}
