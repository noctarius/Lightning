package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.PropertyAccessor;

public interface PropertyAccessorFactory {

	PropertyAccessor fieldAccess(Field field);

	PropertyAccessor methodAccess(Method method);

}
