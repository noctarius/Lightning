package com.github.lightning.internal.beans;

import java.lang.reflect.Method;

import com.github.lightning.PropertyAccessor;

public abstract class MethodPropertyAccessor implements PropertyAccessor {

	private final Method method;

	protected MethodPropertyAccessor(Method method) {
		this.method = method;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return method.getDeclaringClass();
	}

	@Override
	public com.github.lightning.PropertyAccessor.AccessorType getAccessorType() {
		return AccessorType.Method;
	}

	@Override
	public Class<?> getType() {
		return method.getReturnType();
	}

	protected Method getMethod() {
		return method;
	}
}
