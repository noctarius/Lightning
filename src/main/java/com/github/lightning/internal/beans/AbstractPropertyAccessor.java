/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lightning.internal.beans;

import com.github.lightning.PropertyAccessor;

abstract class AbstractPropertyAccessor implements PropertyAccessor {

	@Override
	public void writeShort(Object instance, short value) {
		writeObject(instance, value);
	}

	@Override
	public void writeLong(Object instance, long value) {
		writeObject(instance, value);
	}

	@Override
	public void writeInt(Object instance, int value) {
		writeObject(instance, value);
	}

	@Override
	public void writeFloat(Object instance, float value) {
		writeObject(instance, value);
	}

	@Override
	public void writeDouble(Object instance, double value) {
		writeObject(instance, value);
	}

	@Override
	public void writeChar(Object instance, char value) {
		writeObject(instance, value);
	}

	@Override
	public void writeByte(Object instance, byte value) {
		writeObject(instance, value);
	}

	@Override
	public void writeBoolean(Object instance, boolean value) {
		writeObject(instance, value);
	}

	@Override
	public short readShort(Object instance) {
		return (Short) readObject(instance);
	}

	@Override
	public long readLong(Object instance) {
		return (Long) readObject(instance);
	}

	@Override
	public int readInt(Object instance) {
		return (Integer) readObject(instance);
	}

	@Override
	public float readFloat(Object instance) {
		return (Float) readObject(instance);
	}

	@Override
	public double readDouble(Object instance) {
		return (Double) readObject(instance);
	}

	@Override
	public char readChar(Object instance) {
		return (Character) readObject(instance);
	}

	@Override
	public byte readByte(Object instance) {
		return (Byte) readObject(instance);
	}

	@Override
	public boolean readBoolean(Object instance) {
		return (Boolean) readObject(instance);
	}
}
