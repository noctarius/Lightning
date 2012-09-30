/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.lightning.io;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

import com.github.lightning.Serializer;

/**
 * Parts of this class taken from Hazelcast project
 * 
 * @author noctarius
 */
public class SerializerOutputStream extends FilterOutputStream implements ObjectOutput {

	static final int STRING_CHUNK_SIZE = 16 * 1024;

	private final Serializer serializer;
	private int written = 0;

	public SerializerOutputStream(OutputStream out, Serializer serializer) {
		super(out);
		this.serializer = serializer;
	}

	@Override
	public void writeObject(Object object) {
		serializer.serialize(object, (DataOutput) this);
	}

	@Override
	public synchronized void write(int b) throws IOException {
		out.write(b);
		increaseWritten(1);
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		increaseWritten(len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		out.write(v ? 1 : 0);
	}

	@Override
	public void writeByte(int v) throws IOException {
		out.write(v);
		increaseWritten(1);
	}

	@Override
	public void writeShort(int v) throws IOException {
		out.write((v >>> 8) & 0xFF);
		out.write((v >>> 0) & 0xFF);
		increaseWritten(2);
	}

	@Override
	public void writeChar(int v) throws IOException {
		out.write((v >>> 8) & 0xFF);
		out.write((v >>> 0) & 0xFF);
		increaseWritten(2);
	}

	@Override
	public void writeInt(int v) throws IOException {
		out.write((v >>> 24) & 0xFF);
		out.write((v >>> 16) & 0xFF);
		out.write((v >>> 8) & 0xFF);
		out.write((v >>> 0) & 0xFF);
		increaseWritten(4);
	}

	@Override
	public void writeLong(long v) throws IOException {
		byte[] buffer = new byte[8];
		buffer[0] = (byte) (v >>> 56);
		buffer[1] = (byte) (v >>> 48);
		buffer[2] = (byte) (v >>> 40);
		buffer[3] = (byte) (v >>> 32);
		buffer[4] = (byte) (v >>> 24);
		buffer[5] = (byte) (v >>> 16);
		buffer[6] = (byte) (v >>> 8);
		buffer[7] = (byte) (v >>> 0);
		out.write(buffer, 0, 8);
		increaseWritten(8);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeBytes(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			out.write((byte) s.charAt(i));
		}
		increaseWritten(len);
	}

	@Override
	public void writeChars(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int v = s.charAt(i);
			out.write((v >>> 8) & 0xFF);
			out.write((v >>> 0) & 0xFF);
		}
		increaseWritten(len * 2);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		boolean isNull = (s == null);
		writeBoolean(isNull);
		if (isNull)
			return;
		int length = s.length();
		writeInt(length);
		int chunkSize = length / STRING_CHUNK_SIZE + 1;
		for (int i = 0; i < chunkSize; i++) {
			int beginIndex = Math.max(0, i * STRING_CHUNK_SIZE - 1);
			int endIndex = Math.min((i + 1) * STRING_CHUNK_SIZE - 1, length);
			writeShortUTF(s.substring(beginIndex, endIndex));
		}
	}

	private final void writeShortUTF(final String str) throws IOException {
		final int strlen = str.length();
		int utflen = 0;
		int c, count = 0;
		/* use charAt instead of copying String to char array */
		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			}
			else if (c > 0x07FF) {
				utflen += 3;
			}
			else {
				utflen += 2;
			}
		}
		// if (utflen > 65535)
		// throw new UTFDataFormatException("encoded string too long: " + utflen
		// + " bytes");
		final byte[] bytearr = new byte[utflen + 2];
		bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		bytearr[count++] = (byte) ((utflen) & 0xFF);
		int i;
		for (i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if (!((c >= 0x0001) && (c <= 0x007F)))
				break;
			bytearr[count++] = (byte) c;
		}
		for (; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[count++] = (byte) c;
			}
			else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c) & 0x3F));
			}
			else {
				bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c) & 0x3F));
			}
		}
		write(bytearr, 0, utflen + 2);
	}

	public int size() {
		return written;
	}

	private void increaseWritten(int count) {
		int temp = written + count;
		if (temp < 0) {
			temp = Integer.MAX_VALUE;
		}
		written = temp;
	}
}
