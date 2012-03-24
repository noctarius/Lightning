/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.lightning.exceptions.IllegalPropertyAccessException;
import com.github.lightning.internal.util.BeanUtil;
import com.github.lightning.metadata.PropertyAccessor;

public class ReflectionPropertyAccessorFactory implements PropertyAccessorFactory {

	@Override
	public PropertyAccessor fieldAccess(Field field) {
		return buildForField(field);
	}

	@Override
	public PropertyAccessor methodAccess(Method method) {
		return buildForMethod(method);
	}

	private PropertyAccessor buildForField(final Field field) {
		field.setAccessible(true);
		return new FieldPropertyAccessor(field) {

			@Override
			public <T> void writeObject(Object instance, T value) {
				try {
					getField().set(instance, value);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while writing field " + getField().getName(), e);
				}
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObject(Object instance) {
				try {
					return (T) getField().get(instance);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while reading field " + getField().getName(), e);
				}
			}
		};
	}

	private PropertyAccessor buildForMethod(Method method) {
		Method getter = BeanUtil.findGetterMethod(method);
		Method setter = BeanUtil.findSetterMethod(method);

		getter.setAccessible(true);
		setter.setAccessible(true);

		return new MethodPropertyAccessor(setter, getter) {

			@Override
			public <T> void writeObject(Object instance, T value) {
				try {
					getSetterMethod().invoke(instance, value);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while writing with method " + getSetterMethod().getName(), e);
				}
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObject(Object instance) {
				try {
					return (T) getGetterMethod().invoke(instance);
				}
				catch (Exception e) {
					throw new IllegalPropertyAccessException("Exception while reading with method " + getGetterMethod().getName(), e);
				}
			}
		};
	}
}
