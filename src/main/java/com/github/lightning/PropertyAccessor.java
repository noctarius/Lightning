package com.github.lightning;

public interface PropertyAccessor {

	public static enum AccessorType {
		Field,
		Method
	}

	Class<?> getDeclaringClass();

	AccessorType getAccessorType();

	Class<?> getType();

	<T> void writeObject(Object instance, T value);

	<T> T readObject(Object instance);

	void writeBoolean(Object instance, boolean value);

	boolean readBoolean(Object instance);

	void writeByte(Object instance, byte value);

	byte readByte(Object instance);

	void writeChar(Object instance, char value);

	char readChar(Object instance);

	void writeShort(Object instance, short value);

	short readShort(Object instance);

	void writeInt(Object instance, int value);

	int readInt(Object instance);

	void writeLong(Object instance, long value);

	long readLong(Object instance);

	void writeFloat(Object instance, float value);

	float readFloat(Object instance);

	void writeDouble(Object instance, double value);

	double readDouble(Object instance);
}
