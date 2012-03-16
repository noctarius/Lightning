package com.github.lightning;

public interface PropertyAccessor<T> {

	public static enum AccessorType {
		Field,
		Method
	}

	Class<?> getDeclaringClass();

	AccessorType getAccessorType();

	Class<T> getType();

	void write(T value);

	T read();

}
