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

import com.github.lightning.Marshaller;
import com.github.lightning.internal.util.BeanUtil;
import com.github.lightning.internal.util.StringUtil;
import com.github.lightning.metadata.PropertyAccessor;
import com.github.lightning.metadata.PropertyDescriptor;

class InternalPropertyDescriptor implements PropertyDescriptor {

	private final String name;
	private final String propertyName;
	private final String internalSignature;

	private final PropertyAccessor propertyAccessor;
	private final Marshaller marshaller;

	InternalPropertyDescriptor(String propertyName, Marshaller marshaller, PropertyAccessor propertyAccessor) {
		this.name = StringUtil.toUpperCamelCase(propertyName);
		this.propertyName = propertyName;
		this.propertyAccessor = propertyAccessor;
		this.marshaller = marshaller;
		this.internalSignature = BeanUtil.buildInternalSignature(propertyName, propertyAccessor);
	}

	@Override
	public Class<?> getDeclaringClass() {
		return propertyAccessor.getDeclaringClass();
	}

	@Override
	public PropertyAccessor getPropertyAccessor() {
		return propertyAccessor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public Class<?> getType() {
		return propertyAccessor.getType();
	}

	@Override
	public String getInternalSignature() {
		return internalSignature;
	}

	@Override
	public Marshaller getMarshaller() {
		return marshaller;
	}

	@Override
	public int compareTo(PropertyDescriptor o) {
		return propertyName.compareTo(o.getPropertyName());
	}
}
