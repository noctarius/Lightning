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

import java.lang.reflect.Method;

import com.github.lightning.metadata.AccessorType;

public abstract class MethodArrayPropertyAccessor extends AbstractArrayPropertyAccessor {

	private final Method setter;
	private final Method getter;

	protected MethodArrayPropertyAccessor(Method setter, Method getter) {
		this.setter = setter;
		this.getter = getter;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return getter.getDeclaringClass();
	}

	@Override
	public AccessorType getAccessorType() {
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
