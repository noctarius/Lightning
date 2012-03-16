package com.github.lightning.io;

import java.io.FilterInputStream;
import java.io.InputStream;

public class SerializerInputStream extends FilterInputStream {

	public SerializerInputStream(InputStream in) {
		super(in);
	}

	public <T> T readObject() {
		return null;
	}

}
