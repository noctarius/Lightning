package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.Marshaller;
import com.github.lightning.PropertyAccessor;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.util.BeanUtil;

public class PropertyDescriptorFactory {

	private PropertyDescriptorFactory() {
	}

	public static PropertyDescriptor byMethod(Method method, Marshaller<?> marshaller) {
		PropertyAccessor propertyAccessor = PropertyAccessorStrategy.byMethod(method);
		String propertyName = BeanUtil.buildPropertyName(method);
		return new InternalPropertyDescriptor(propertyName, marshaller, propertyAccessor);
	}

	public static PropertyDescriptor byField(Field field, Marshaller<?> marshaller) {
		PropertyAccessor propertyAccessor = PropertyAccessorStrategy.byField(field);
		return new InternalPropertyDescriptor(field.getName(), marshaller, propertyAccessor);
	}
}
