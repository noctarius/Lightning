package com.github.lightning;

public interface PropertyDescriptor<T> {

	Class<?> getDeclaringClass();

	PropertyAccessor<T> getPropertyAccessor();

	String getName();

	String getPropertyName();

	Class<?> getType();

	String getInternalSignature();

}
