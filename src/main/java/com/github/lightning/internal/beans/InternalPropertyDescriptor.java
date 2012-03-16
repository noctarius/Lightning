package com.github.lightning.internal.beans;

import com.github.lightning.PropertyAccessor;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.util.BeanUtil;
import com.github.lightning.internal.util.StringUtil;

class InternalPropertyDescriptor implements PropertyDescriptor, Comparable<PropertyDescriptor> {

	private final String name;
	private final String propertyName;
	private final String internalSignature;

	private final PropertyAccessor propertyAccessor;

	InternalPropertyDescriptor(String propertyName, PropertyAccessor propertyAccessor) {
		this.name = StringUtil.toUpperCamelCase(propertyName);
		this.propertyName = propertyName;
		this.propertyAccessor = propertyAccessor;
		this.internalSignature = BeanUtil.buildInternalSignature(propertyName, propertyAccessor);
	}

	@Override
	public Class<?> getDeclaringClass() {
		return propertyAccessor.getDeclaringClass();
	}

	@Override
	public PropertyAccessor getPropertyAccessor() {
		return propertyAccessor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public Class<?> getType() {
		return propertyAccessor.getType();
	}

	@Override
	public String getInternalSignature() {
		return internalSignature;
	}

	@Override
	public int compareTo(PropertyDescriptor o) {
		return propertyName.compareTo(o.getPropertyName());
	}
}
