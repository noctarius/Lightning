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
package com.github.lightning.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.lightning.Attribute;
import com.github.lightning.DefinitionBuildingContext;
import com.github.lightning.DefinitionVisitor;
import com.github.lightning.Marshaller;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.SerializerDefinition;
import com.github.lightning.SerializerDefinitionException;
import com.github.lightning.bindings.AnnotatedBinder;
import com.github.lightning.bindings.ClassBinder;
import com.github.lightning.bindings.MarshallerBinder;
import com.github.lightning.bindings.PropertyBinder;
import com.github.lightning.internal.instantiator.ObjenesisSerializer;
import com.github.lightning.internal.util.BeanUtil;

public abstract class AbstractSerializerDefinition implements SerializerDefinition {

	private final Map<Class<?>, Marshaller> marshallers = new HashMap<Class<?>, Marshaller>();
	private final Set<SerializerDefinition> children = new HashSet<SerializerDefinition>();
	private final Map<PropertyDescriptor, Marshaller> propertyMarshallers = new HashMap<PropertyDescriptor, Marshaller>();
	private final Map<AnnotatedBinder, AnnotationBinderDefinition<?>> annotationBinders = new HashMap<AnnotatedBinder, AnnotationBinderDefinition<?>>();

	private DefinitionBuildingContext definitionBuildingContext;
	private ObjenesisSerializer objenesisSerializer = null;
	private Class<? extends Annotation> attributeAnnotation = null;
	private AbstractSerializerDefinition parent = null;

	@Override
	public final void configure(DefinitionBuildingContext definitionBuildingContext, ObjenesisSerializer objenesisSerializer) {
		// Save PropertyDescriptorFactory for later use in configure()
		this.definitionBuildingContext = definitionBuildingContext;

		// Save ObjenesisSerializer for later use in configure()
		this.objenesisSerializer = objenesisSerializer;

		// Read the configuration
		configure();
	}

	@Override
	public final void acceptVisitor(DefinitionVisitor visitor) {
		// Start visiting
		visitor.visitSerializerDefinition(this);

		// Visit the attribute annotation if set
		Class<? extends Annotation> attributeAnnotation = findAttributeAnnotation(this);
		if (attributeAnnotation != null) {
			visitor.visitAttributeAnnotation(attributeAnnotation);
		}

		// Visit all direct marshallers
		for (Entry<Class<?>, Marshaller> entry : marshallers.entrySet()) {
			visitor.visitClassDefine(entry.getKey(), entry.getValue());
		}

		// Visit annotated properties
		for (AnnotationBinderDefinition<?> annotationBinderDefinition : annotationBinders.values()) {
			annotationBinderDefinition.acceptVisitor(visitor);
		}

		// Visit all property definitions
		for (Entry<PropertyDescriptor, Marshaller> entry : propertyMarshallers.entrySet()) {
			visitor.visitPropertyDescriptor(entry.getKey(), entry.getValue());
		}

		// Visit all children
		for (SerializerDefinition child : children) {
			child.configure(definitionBuildingContext, objenesisSerializer);
			child.acceptVisitor(visitor);
		}

		// Finalize visit
		visitor.visitFinalizeSerializerDefinition(this);
	}

	protected abstract void configure();

	protected <T> ClassBinder<T> bind(final Class<T> clazz) {
		return buildClassBinder(clazz);
	}

	protected void install(SerializerDefinition childSerializer) {
		children.add(childSerializer);
		if (childSerializer instanceof AbstractSerializerDefinition) {
			((AbstractSerializerDefinition) childSerializer).parent = this;
		}
	}

	protected <T> MarshallerBinder define(final Class<T> clazz) {
		return buildMarshallerBinder(clazz);
	}

	protected void describesAttributes(Class<? extends Annotation> attributeAnnotation) {
		this.attributeAnnotation = attributeAnnotation;
	}

	private <T> MarshallerBinder buildMarshallerBinder(final Class<T> clazz) {
		return new MarshallerBinder() {

			@Override
			public void byMarshaller(Class<? extends Marshaller> marshaller) {
				try {
					byMarshaller(marshaller.newInstance());
				}
				catch (Exception e) {
					throw new SerializerDefinitionException("Marshaller class " + marshaller.getCanonicalName()
							+ " could not be instantiated. Is there a standard (public) constructor?", e);
				}
			}

			@Override
			public void byMarshaller(Marshaller marshaller) {
				if (marshaller instanceof AbstractObjectMarshaller) {
					marshallers.put(clazz, new ObjenesisDelegatingMarshaller((AbstractObjectMarshaller) marshaller, objenesisSerializer));
				}
				else {
					marshallers.put(clazz, marshaller);
				}
			}
		};
	}

	private <T> ClassBinder<T> buildClassBinder(final Class<T> clazz) {
		return new ClassBinder<T>() {

			@Override
			public AnnotatedBinder attributes() {
				return buildAnnotatedBinder(this, attributeAnnotation);
			}

			@Override
			public AnnotatedBinder with(Class<? extends Annotation> annotation) {
				return buildAnnotatedBinder(this, annotation);
			}

			@Override
			public <V> PropertyBinder<V> property(String property) {
				try {
					Field reflectiveField = clazz.getDeclaredField(property);
					reflectiveField.setAccessible(true);
					return buildPropertyBinder(this, reflectiveField);
				}
				catch (Exception e) {
					throw new SerializerDefinitionException("Property " + property + " could not be found for type "
							+ clazz.getCanonicalName(), e);
				}
			}

			@Override
			public <V> PropertyBinder<V> property(Field property) {
				return buildPropertyBinder(this, property);
			}

			@Override
			public Class<T> getType() {
				return clazz;
			}
		};
	}

	private <T> AnnotatedBinder buildAnnotatedBinder(final ClassBinder<T> classBinder,
			final Class<? extends Annotation> annotation) {

		return new AnnotatedBinder() {

			private final AnnotationBinderDefinition<T> binder = new AnnotationBinderDefinition<T>(classBinder);

			{
				annotationBinders.put(this, binder);
			}

			@Override
			public AnnotatedBinder exclude(String property) {
				binder.addExclude(property);
				return this;
			}
		};
	}

	private <T, V> PropertyBinder<V> buildPropertyBinder(final ClassBinder<T> classBinder, final Field property) {
		return new PropertyBinder<V>() {

			@Override
			public void byMarshaller(Class<? extends Marshaller> marshaller) {
				try {
					byMarshaller(marshaller.newInstance());
				}
				catch (Exception e) {
					throw new SerializerDefinitionException("Marshaller class " + marshaller.getCanonicalName()
							+ " could not be instantiated. Is there a standard (public) constructor?", e);
				}
			}

			@Override
			public void byMarshaller(Marshaller marshaller) {
				propertyMarshallers.put(definitionBuildingContext.getPropertyDescriptorFactory().byField(property, marshaller), marshaller);
			}
		};
	}

	private Class<? extends Annotation> findAttributeAnnotation(AbstractSerializerDefinition abstractSerializerDefinition) {
		if (attributeAnnotation != null) {
			return attributeAnnotation;
		}

		if (parent != null) {
			return findAttributeAnnotation(parent);
		}

		return Attribute.class;
	}

	private class AnnotationBinderDefinition<T> {

		private final ClassBinder<T> classBinder;
		private final List<String> excludes = new ArrayList<String>();

		private AnnotationBinderDefinition(ClassBinder<T> classBinder) {
			this.classBinder = classBinder;
		}

		public void addExclude(String exclude) {
			excludes.add(exclude);
		}

		public void acceptVisitor(DefinitionVisitor visitor) {
			Class<? extends Annotation> attributeAnnotation = findAttributeAnnotation(AbstractSerializerDefinition.this);
			Class<T> type = classBinder.getType();
			List<Field> fields = findFields(type, attributeAnnotation);
			fields.addAll(findByMethods(type, attributeAnnotation));

			for (Field field : fields) {
				Class<?> fieldType = field.getType();

				Marshaller marshaller = definitionBuildingContext.getMarshallerStrategy().getMarshaller(fieldType, marshallers);
				if (marshaller == null) {
					throw new SerializerDefinitionException("Field " + field + " cannot be marshalled");
				}

				PropertyDescriptor propertyDescriptor = definitionBuildingContext.getPropertyDescriptorFactory().byField(field, marshaller);
				visitor.visitAnnotatedAttribute(propertyDescriptor, marshaller);
			}
		}

		private List<Field> findFields(Class<?> type, Class<? extends Annotation> attributeAnnotation) {
			List<Field> attributes = new ArrayList<Field>();
			for (Field field : type.getDeclaredFields()) {
				if (field.isAnnotationPresent(attributeAnnotation)) {
					attributes.add(field);
				}
			}

			return attributes;
		}

		private List<Field> findByMethods(Class<?> type, Class<? extends Annotation> attributeAnnotation) {
			List<Field> attributes = new ArrayList<Field>();
			for (Method method : type.getDeclaredMethods()) {
				if (method.isAnnotationPresent(attributeAnnotation)) {
					String propertyName = BeanUtil.buildPropertyName(method);
					Field field = BeanUtil.getFieldByPropertyName(propertyName, type);
					if (field == null) {
						if (attributeAnnotation == Attribute.class) {
							Attribute attribute = method.getAnnotation(Attribute.class);
							field = BeanUtil.getFieldByPropertyName(attribute.property(), type);
						}

						if (field == null) {
							throw new SerializerDefinitionException("No property for method " + method + " was found");
						}
					}

					attributes.add(field);
				}
			}

			return attributes;
		}
	}
}
