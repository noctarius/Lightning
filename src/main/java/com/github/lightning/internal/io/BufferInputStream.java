package com.github.lightning.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferInputStream extends InputStream {

	private final ByteBuffer byteBuffer;

	public BufferInputStream(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}

	public synchronized int read() throws IOException {
		if (!byteBuffer.hasRemaining()) {
			return -1;
		}
		return byteBuffer.get();
	}

	public synchronized int read(byte[] bytes, int off, int len) throws IOException {
		len = Math.min(len, byteBuffer.remaining());
		byteBuffer.get(bytes, off, len);
		return len;
	}
}
