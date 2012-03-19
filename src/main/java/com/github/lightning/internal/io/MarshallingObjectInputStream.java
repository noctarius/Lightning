package com.github.lightning.internal.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

public class MarshallingObjectInputStream extends DataInputStream implements ObjectInput {

	public MarshallingObjectInputStream(InputStream in) {
		super(in);
	}

	@Override
	public Object readObject() throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
