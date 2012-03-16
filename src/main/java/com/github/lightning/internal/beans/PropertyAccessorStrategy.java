package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.PropertyAccessor;
import com.github.lightning.internal.util.InternalUtil;

public class PropertyAccessorStrategy {

	private static final PropertyAccessorFactory SUN_UNSAGE_PROPERTY_ACCESSOR_FACTORY;

	static {
		PropertyAccessorFactory factory = null;
		if (InternalUtil.isUnsafeAvailable()) {
			factory = InternalUtil.buildSunUnsafePropertyAccessor();
		}
		SUN_UNSAGE_PROPERTY_ACCESSOR_FACTORY = factory;
	}

	static PropertyAccessor byField(Field field) {
		if (SUN_UNSAGE_PROPERTY_ACCESSOR_FACTORY != null) {
			return SUN_UNSAGE_PROPERTY_ACCESSOR_FACTORY.fieldAccess(field);
		}
		return null;
	}

	static PropertyAccessor byMethod(Method method) {
		return null;
	}
}
