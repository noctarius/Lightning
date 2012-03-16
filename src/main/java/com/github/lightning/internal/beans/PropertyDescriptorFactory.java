package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.PropertyAccessor;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.util.BeanUtil;

public class PropertyDescriptorFactory {

	private PropertyDescriptorFactory() {
	}

	public static PropertyDescriptor byMethod(Method method) {
		PropertyAccessor propertyAccessor = PropertyAccessorStrategy.byMethod(method);
		String propertyName = BeanUtil.buildPropertyName(method);
		return new InternalPropertyDescriptor(propertyName, propertyAccessor);
	}

	public static PropertyDescriptor byField(Field field) {
		PropertyAccessor propertyAccessor = PropertyAccessorStrategy.byField(field);
		return new InternalPropertyDescriptor(field.getName(), propertyAccessor);
	}
}
