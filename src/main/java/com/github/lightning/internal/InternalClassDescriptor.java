package com.github.lightning.internal;

import java.util.ArrayList;
import java.util.List;

import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDescriptor;
import com.github.lightning.Marshaller;
import com.github.lightning.PropertyDescriptor;

class InternalClassDescriptor implements ClassDescriptor {

	private final List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();
	private final Class<?> type;

	private ClassDefinition classDefinition;
	private Marshaller<?> marshaller;

	InternalClassDescriptor(Class<?> type) {
		this.type = type;
	}

	@Override
	public ClassDefinition getClassDefinition() {
		return classDefinition;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public List<PropertyDescriptor> getPropertyDescriptors() {
		return propertyDescriptors;
	}

	@Override
	public Marshaller<?> getMarshaller() {
		return marshaller;
	}

	public void push(PropertyDescriptor propertyDescriptor) {
		propertyDescriptors.add(propertyDescriptor);
	}

	public void setMarshaller(Marshaller<?> marshaller) {
		this.marshaller = marshaller;
	}

	public void build() {
		classDefinition = new InternalClassDefinition(getType(), getPropertyDescriptors());
	}
}
