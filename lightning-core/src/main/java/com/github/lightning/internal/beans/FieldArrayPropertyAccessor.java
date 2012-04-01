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

import com.github.lightning.metadata.AccessorType;
import com.github.lightning.metadata.ArrayPropertyAccessor;

public abstract class FieldArrayPropertyAccessor extends FieldValuePropertyAccessor implements ArrayPropertyAccessor {

	private final Field field;

	protected FieldArrayPropertyAccessor(Field field) {
		super(field);
		this.field = field;
	}

	@Override
	public boolean isArrayType() {
		return true;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return field.getDeclaringClass();
	}

	@Override
	public AccessorType getAccessorType() {
		return AccessorType.Field;
	}

	@Override
	public Class<?> getType() {
		return field.getType();
	}

	protected Field getField() {
		return field;
	}
}
