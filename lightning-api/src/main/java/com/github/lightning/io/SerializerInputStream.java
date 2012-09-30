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

import java.io.DataInput;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.PushbackInputStream;
import java.io.UTFDataFormatException;

import com.github.lightning.Serializer;

/**
 * Parts of this class taken from Hazelcast project
 * 
 * @author noctarius
 */
public class SerializerInputStream extends FilterInputStream implements ObjectInput {

	private final Serializer serializer;
	private final byte[] longReadBuffer = new byte[8];
	private char lineBuffer[];

	public SerializerInputStream(InputStream in, Serializer serializer) {
		super(in);
		this.serializer = serializer;
	}

	@Override
	public Object readObject() {
		return serializer.deserialize((DataInput) this);
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		in.read(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		in.read(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return (int) in.skip(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return in.read() == 1;
	}

	@Override
	public byte readByte() throws IOException {
		return (byte) in.read();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return in.read();
	}

	@Override
	public short readShort() throws IOException {
		int byte1 = read();
		int byte2 = read();
		return (short) ((byte1 << 8) + (byte2 << 0));
	}

	@Override
	public int readUnsignedShort() throws IOException {
		int byte1 = read();
		int byte2 = read();
		return (short) ((byte1 << 8) + (byte2 << 0));
	}

	@Override
	public char readChar() throws IOException {
		return (char) readUnsignedShort();
	}

	@Override
	public int readInt() throws IOException {
		final int byte1 = read();
		final int byte2 = read();
		final int byte3 = read();
		final int byte4 = read();
		return ((byte1 << 24) + (byte2 << 16) + (byte3 << 8) + (byte4 << 0));
	}

	@Override
	public long readLong() throws IOException {
		in.read(longReadBuffer);
		return (((long) longReadBuffer[0] << 56) + ((long) (longReadBuffer[1] & 255) << 48) + ((long) (longReadBuffer[2] & 255) << 40)
				+ ((long) (longReadBuffer[3] & 255) << 32) + ((long) (longReadBuffer[4] & 255) << 24) + ((longReadBuffer[5] & 255) << 16)
				+ ((longReadBuffer[6] & 255) << 8) + ((longReadBuffer[7] & 255) << 0));
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public String readLine() throws IOException {
		char buf[] = lineBuffer;
		if (buf == null) {
			buf = lineBuffer = new char[128];
		}
		int room = buf.length;
		int offset = 0;
		int c;
		loop: while (true) {
			switch (c = read()) {
				case -1:
				case '\n':
					break loop;
				case '\r':
					final int c2 = read();
					if ((c2 != '\n') && (c2 != -1)) {
						new PushbackInputStream(this).unread(c2);
					}
					break loop;
				default:
					if (--room < 0) {
						buf = new char[offset + 128];
						room = buf.length - offset - 1;
						System.arraycopy(lineBuffer, 0, buf, 0, offset);
						lineBuffer = buf;
					}
					buf[offset++] = (char) c;
					break;
			}
		}
		if ((c == -1) && (offset == 0)) {
			return null;
		}
		return String.copyValueOf(buf, 0, offset);

	}

	@Override
	public String readUTF() throws IOException {
		boolean isNull = readBoolean();
		if (isNull)
			return null;
		int length = readInt();
		StringBuilder result = new StringBuilder(length);
		int chunkSize = length / SerializerOutputStream.STRING_CHUNK_SIZE + 1;
		while (chunkSize > 0) {
			result.append(readShortUTF());
			chunkSize--;
		}
		return result.toString();
	}

	private final String readShortUTF() throws IOException {
		final int utflen = readShort();
		byte[] bytearr = null;
		char[] chararr = null;
		bytearr = new byte[utflen];
		chararr = new char[utflen];
		int c, char2, char3;
		int count = 0;
		int chararr_count = 0;
		readFully(bytearr, 0, utflen);
		while (count < utflen) {
			c = bytearr[count] & 0xff;
			if (c > 127)
				break;
			count++;
			chararr[chararr_count++] = (char) c;
		}
		while (count < utflen) {
			c = bytearr[count] & 0xff;
			switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					/* 0xxxxxxx */
					count++;
					chararr[chararr_count++] = (char) c;
					break;
				case 12:
				case 13:
					/* 110x xxxx 10xx xxxx */
					count += 2;
					if (count > utflen)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = bytearr[count - 1];
					if ((char2 & 0xC0) != 0x80)
						throw new UTFDataFormatException("malformed input around byte " + count);
					chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
					break;
				case 14:
					/* 1110 xxxx 10xx xxxx 10xx xxxx */
					count += 3;
					if (count > utflen)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = bytearr[count - 2];
					char3 = bytearr[count - 1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
						throw new UTFDataFormatException("malformed input around byte " + (count - 1));
					chararr[chararr_count++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
					break;
				default:
					/* 10xx xxxx, 1111 xxxx */
					throw new UTFDataFormatException("malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}
}
