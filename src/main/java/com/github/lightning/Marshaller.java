package com.github.lightning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Marshaller {

	boolean acceptType(Class<?> type);

	void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException;

	<V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException;

}
