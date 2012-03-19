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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.PropertyAccessor;
import com.github.lightning.internal.util.UnsafeUtil;

@SuppressWarnings("restriction")
final class SunUnsafePropertyAccessorFactory implements PropertyAccessorFactory {

	private static final sun.misc.Unsafe UNSAFE = UnsafeUtil.getUnsafe();

	SunUnsafePropertyAccessorFactory() {
	}

	@Override
	public PropertyAccessor fieldAccess(Field field) {
		return buildForField(field);
	}

	@Override
	public PropertyAccessor methodAccess(Method method) {
		throw new UnsupportedOperationException("Method access is not supported by Unsafe style");
	}

	private PropertyAccessor buildForField(final Field field) {
		return new FieldPropertyAccessor(field) {

			private final long offset;

			{
				offset = UNSAFE.objectFieldOffset(field);
			}

			@Override
			public <T> void writeObject(Object instance, T value) {
				UNSAFE.putObject(instance, offset, value);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObject(Object instance) {
				return (T) UNSAFE.getObject(instance, offset);
			}

			@Override
			public void writeBoolean(Object instance, boolean value) {
				UNSAFE.putBoolean(instance, offset, value);
			}

			@Override
			public boolean readBoolean(Object instance) {
				return UNSAFE.getBoolean(instance, offset);
			}

			@Override
			public void writeByte(Object instance, byte value) {
				UNSAFE.putByte(instance, offset, value);
			}

			@Override
			public byte readByte(Object instance) {
				return UNSAFE.getByte(instance, offset);
			}

			@Override
			public void writeShort(Object instance, short value) {
				UNSAFE.putShort(instance, offset, value);
			}

			@Override
			public short readShort(Object instance) {
				return UNSAFE.getShort(instance, offset);
			}

			@Override
			public void writeChar(Object instance, char value) {
				UNSAFE.putChar(instance, offset, value);
			}

			@Override
			public char readChar(Object instance) {
				return UNSAFE.getChar(instance, offset);
			}

			@Override
			public void writeInt(Object instance, int value) {
				UNSAFE.putInt(instance, offset, value);
			}

			@Override
			public int readInt(Object instance) {
				return UNSAFE.getInt(instance, offset);
			}

			@Override
			public void writeLong(Object instance, long value) {
				UNSAFE.putLong(instance, offset, value);
			}

			@Override
			public long readLong(Object instance) {
				return UNSAFE.getLong(instance, offset);
			}

			@Override
			public void writeFloat(Object instance, float value) {
				UNSAFE.putFloat(instance, offset, value);
			}

			@Override
			public float readFloat(Object instance) {
				return UNSAFE.getFloat(instance, offset);
			}

			@Override
			public void writeDouble(Object instance, double value) {
				UNSAFE.putDouble(instance, offset, value);
			}

			@Override
			public double readDouble(Object instance) {
				return UNSAFE.getDouble(instance, offset);
			}
		};
	}
}
