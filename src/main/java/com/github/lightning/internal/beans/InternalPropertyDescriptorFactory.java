package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.Marshaller;
import com.github.lightning.PropertyAccessor;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.PropertyDescriptorFactory;
import com.github.lightning.internal.util.BeanUtil;
import com.github.lightning.logging.Logger;

public class InternalPropertyDescriptorFactory implements PropertyDescriptorFactory {

	private final PropertyAccessorStrategy propertyAccessorStrategy;

	public InternalPropertyDescriptorFactory(Logger logger) {
		propertyAccessorStrategy = new PropertyAccessorStrategy(logger);
	}

	public PropertyDescriptor byMethod(Method method, Marshaller marshaller) {
		PropertyAccessor propertyAccessor = propertyAccessorStrategy.byMethod(method);
		String propertyName = BeanUtil.buildPropertyName(method);
		return new InternalPropertyDescriptor(propertyName, marshaller, propertyAccessor);
	}

	public PropertyDescriptor byField(Field field, Marshaller marshaller) {
		PropertyAccessor propertyAccessor = propertyAccessorStrategy.byField(field);
		return new InternalPropertyDescriptor(field.getName(), marshaller, propertyAccessor);
	}
}
