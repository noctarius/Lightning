package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.PropertyAccessor;
import com.github.lightning.internal.util.InternalUtil;
import com.github.lightning.logging.Logger;

public class PropertyAccessorStrategy {

	private final PropertyAccessorFactory reflectionPropertyAccessorFactory = new ReflectionPropertyAccessorFactory();
	private final PropertyAccessorFactory reflectASMPropertyAccessorFactory = new ReflectASMPropertyAccessorFactory();
	private final PropertyAccessorFactory sunUnsafePropertyAccessorFactory;

	private final Logger logger;

	PropertyAccessorStrategy(Logger logger) {
		this.logger = logger.getChildLogger(getClass());

		PropertyAccessorFactory factory = null;
		if (InternalUtil.isUnsafeAvailable()) {
			factory = InternalUtil.buildSunUnsafePropertyAccessor();
			this.logger.trace("Found sun.misc.Unsafe");
		}
		sunUnsafePropertyAccessorFactory = factory;
	}

	PropertyAccessor byField(Field field) {
		PropertyAccessor propertyAccessor = null;
		if (sunUnsafePropertyAccessorFactory != null) {
			propertyAccessor = sunUnsafePropertyAccessorFactory.fieldAccess(field);
		}

		if (propertyAccessor == null) {
			propertyAccessor = reflectASMPropertyAccessorFactory.fieldAccess(field);
		}

		if (propertyAccessor != null) {
			return propertyAccessor;
		}

		return reflectionPropertyAccessorFactory.fieldAccess(field);
	}

	PropertyAccessor byMethod(Method method) {
		PropertyAccessor propertyAccessor = reflectASMPropertyAccessorFactory.methodAccess(method);
		if (propertyAccessor != null) {
			return propertyAccessor;
		}

		return reflectionPropertyAccessorFactory.methodAccess(method);
	}
}
