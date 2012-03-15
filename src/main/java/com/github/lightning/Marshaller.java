package com.github.lightning;

import java.io.DataInput;
import java.io.DataOutput;

public interface Marshaller<V> {

	void marshall(V value, DataOutput dataOutput);

	V unmarshall(V value, DataInput dataInput);

}
