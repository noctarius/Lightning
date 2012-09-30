/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.lightning.internal.beans.introspection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.github.lightning.Marshaller;
import com.github.lightning.MarshallerContext;
import com.github.lightning.MarshallerStrategy;
import com.github.lightning.TypeBindableMarshaller;
import com.github.lightning.configuration.TypeIntrospector;
import com.github.lightning.generator.PropertyDescriptorFactory;
import com.github.lightning.internal.util.BeanUtil;
import com.github.lightning.internal.util.TypeUtil;
import com.github.lightning.metadata.PropertyDescriptor;

public class AnnotatedTypeIntrospector implements TypeIntrospector {

	private final Class<? extends Annotation> annotationType;
	private final List<String> excludes;

	public AnnotatedTypeIntrospector(Class<? extends Annotation> annotationType, List<String> excludes) {
		this.annotationType = annotationType;
		this.excludes = excludes;
	}

	@Override
	public List<PropertyDescriptor> introspect(Type type, MarshallerStrategy marshallerStrategy, MarshallerContext marshallerContext,
			PropertyDescriptorFactory propertyDescriptorFactory) {

		if (!(type instanceof Class)) {
			return Collections.emptyList();
		}

		Class<?> clazz = (Class<?>) type;
		Set<Field> properties = BeanUtil.findPropertiesByClass(clazz, annotationType);

		List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();
		for (Field property : properties) {
			if (isExcluded(property.getName())) {
				continue;
			}

			Class<?> fieldType = property.getType();

			Marshaller marshaller = marshallerStrategy.getMarshaller(fieldType, marshallerContext, false);

			if (marshaller == null && fieldType.isArray()) {
				marshaller = marshallerStrategy.getMarshaller(fieldType.getComponentType(), marshallerContext, false);
			}

			if (marshaller instanceof TypeBindableMarshaller) {
				Type[] typeArguments = TypeUtil.getTypeArgument(property.getGenericType());
				marshaller = ((TypeBindableMarshaller) marshaller).bindType(typeArguments);
			}

			propertyDescriptors.add(propertyDescriptorFactory.byField(property, marshaller, clazz));
		}

		return propertyDescriptors;
	}

	private boolean isExcluded(String propertyName) {
		return excludes.contains(propertyName);
	}

}
