package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.IllegalPropertyAccessException;
import com.github.lightning.PropertyAccessor;

public class ReflectionPropertyAccessorFactory implements PropertyAccessorFactory {

	@Override
	public PropertyAccessor fieldAccess(Field field) {
		return buildForField(field);
	}

	@Override
	public PropertyAccessor methodAccess(Method method) {
		return buildForMethod(method);
	}

	private PropertyAccessor buildForField(final Field field) {
		field.setAccessible(true);
		return new FieldPropertyAccessor(field) {

			@Override
			public void writeShort(Object instance, short value) {
				writeObject(instance, value);
			}

			@Override
			public <T> void writeObject(Object instance, T value) {
				try {
					getField().set(instance, value);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while writing field " + getField().getName(), e);
				}
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
				return readObject(instance);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObject(Object instance) {
				try {
					return (T) getField().get(instance);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while reading field " + getField().getName(), e);
				}
			}

			@Override
			public long readLong(Object instance) {
				return readObject(instance);
			}

			@Override
			public int readInt(Object instance) {
				return readObject(instance);
			}

			@Override
			public float readFloat(Object instance) {
				return readObject(instance);
			}

			@Override
			public double readDouble(Object instance) {
				return readObject(instance);
			}

			@Override
			public char readChar(Object instance) {
				return readObject(instance);
			}

			@Override
			public byte readByte(Object instance) {
				return readObject(instance);
			}

			@Override
			public boolean readBoolean(Object instance) {
				return readObject(instance);
			}
		};
	}

	private PropertyAccessor buildForMethod(final Method method) {
		method.setAccessible(true);
		return new MethodPropertyAccessor(method) {

			@Override
			public void writeShort(Object instance, short value) {
				writeObject(instance, value);
			}

			@Override
			public <T> void writeObject(Object instance, T value) {
				try {
					method.invoke(instance, value);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while writing with method " + getMethod().getName(), e);
				}
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
				return readObject(instance);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObject(Object instance) {
				try {
					return (T) getMethod().invoke(instance);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while reading with method " + getMethod().getName(), e);
				}
			}

			@Override
			public long readLong(Object instance) {
				return readObject(instance);
			}

			@Override
			public int readInt(Object instance) {
				return readObject(instance);
			}

			@Override
			public float readFloat(Object instance) {
				return readObject(instance);
			}

			@Override
			public double readDouble(Object instance) {
				return readObject(instance);
			}

			@Override
			public char readChar(Object instance) {
				return readObject(instance);
			}

			@Override
			public byte readByte(Object instance) {
				return readObject(instance);
			}

			@Override
			public boolean readBoolean(Object instance) {
				return readObject(instance);
			}
		};
	}
}
