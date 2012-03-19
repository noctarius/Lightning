package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.Streamed;
import com.github.lightning.base.AbstractObjectMarshaller;

public class StreamedMarshaller extends AbstractObjectMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return Streamed.class.isAssignableFrom(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		((Streamed) value).writeTo(dataOutput);
	}

	@Override
	public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
		((Streamed) value).readFrom(dataInput);
		return value;
	}
}
