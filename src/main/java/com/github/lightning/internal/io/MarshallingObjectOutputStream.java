package com.github.lightning.internal.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

public class MarshallingObjectOutputStream extends DataOutputStream implements ObjectOutput {

	public MarshallingObjectOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void writeObject(Object obj) throws IOException {
		// TODO Auto-generated method stub

	}

}
