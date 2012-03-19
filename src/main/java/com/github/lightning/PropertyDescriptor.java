package com.github.lightning;

public interface PropertyDescriptor extends Comparable<PropertyDescriptor> {

	Class<?> getDeclaringClass();

	PropertyAccessor getPropertyAccessor();

	String getName();

	String getPropertyName();

	Class<?> getType();

	String getInternalSignature();

	Marshaller getMarshaller();

}
