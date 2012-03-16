package com.github.lightning.internal.beans;

import java.lang.reflect.Field;

import com.github.lightning.PropertyAccessor;

public abstract class FieldPropertyAccessor implements PropertyAccessor {

	private final Field field;

	protected FieldPropertyAccessor(Field field) {
		this.field = field;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return field.getDeclaringClass();
	}

	@Override
	public com.github.lightning.PropertyAccessor.AccessorType getAccessorType() {
		return AccessorType.Field;
	}

	@Override
	public Class<?> getType() {
		return field.getType();
	}

	protected Field getField() {
		return field;
	}
}
