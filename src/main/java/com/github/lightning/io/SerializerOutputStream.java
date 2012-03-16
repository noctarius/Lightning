package com.github.lightning.io;

import java.io.FilterOutputStream;
import java.io.OutputStream;

public class SerializerOutputStream extends FilterOutputStream {

	public SerializerOutputStream(OutputStream out) {
		super(out);
	}

	public void writeObject(Object object) {

	}
}
