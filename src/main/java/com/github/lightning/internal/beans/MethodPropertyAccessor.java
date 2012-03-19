package com.github.lightning.internal.beans;

import java.lang.reflect.Method;

import com.github.lightning.PropertyAccessor;

public abstract class MethodPropertyAccessor implements PropertyAccessor {

	private final Method setter;
	private final Method getter;

	protected MethodPropertyAccessor(Method setter, Method getter) {
		this.setter = setter;
		this.getter = getter;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return getter.getDeclaringClass();
	}

	@Override
	public com.github.lightning.PropertyAccessor.AccessorType getAccessorType() {
		return AccessorType.Method;
	}

	@Override
	public Class<?> getType() {
		return getter.getReturnType();
	}

	protected Method getGetterMethod() {
		return getter;
	}

	protected Method getSetterMethod() {
		return setter;
	}
}
