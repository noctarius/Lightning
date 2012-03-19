package com.github.lightning;

import java.util.List;

public interface ClassDescriptor {

	ClassDefinition getClassDefinition();

	Class<?> getType();

	List<PropertyDescriptor> getPropertyDescriptors();

	Marshaller getMarshaller();

}
