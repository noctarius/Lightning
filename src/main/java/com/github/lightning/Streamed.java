package com.github.lightning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Streamed {

	void writeTo(DataOutput dataOutput) throws IOException;

	void readFrom(DataInput dataInput) throws IOException;

}
