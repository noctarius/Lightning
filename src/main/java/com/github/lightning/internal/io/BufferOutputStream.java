package com.github.lightning.internal.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class BufferOutputStream extends OutputStream {

	private final ByteBuffer byteBuffer;

	public BufferOutputStream(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}

	public synchronized void write(int b) throws IOException {
		byteBuffer.put((byte) b);
	}

	public synchronized void write(byte[] bytes, int off, int len) throws IOException {
		byteBuffer.put(bytes, off, len);
	}
}
