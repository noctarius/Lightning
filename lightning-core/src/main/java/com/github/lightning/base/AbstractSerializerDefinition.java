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
package com.github.lightning.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.github.lightning.Marshaller;
import com.github.lightning.MarshallerContext;
import com.github.lightning.TypeBindableMarshaller;
import com.github.lightning.bindings.AnnotatedBinder;
import com.github.lightning.bindings.AttributeBinder;
import com.github.lightning.bindings.ClassBinder;
import com.github.lightning.configuration.SerializerDefinition;
import com.github.lightning.configuration.TypeIntrospector;
import com.github.lightning.exceptions.SerializerDefinitionException;
import com.github.lightning.generator.DefinitionBuildingContext;
import com.github.lightning.generator.DefinitionVisitor;
import com.github.lightning.instantiator.ObjectInstantiatorFactory;
import com.github.lightning.internal.InternalMarshallerContext;
import com.github.lightning.internal.beans.introspection.AnnotatedTypeIntrospector;
import com.github.lightning.internal.util.TypeUtil;
import com.github.lightning.metadata.Attribute;
import com.github.lightning.metadata.PropertyDescriptor;

public abstract class AbstractSerializerDefinition implements SerializerDefinition {

	private final InternalMarshallerContext marshallerContext = new InternalMarshallerContext();
	private final Set<SerializerDefinition> children = new HashSet<SerializerDefinition>();
	private final Map<PropertyDescriptor, Marshaller> propertyMarshallers = new HashMap<PropertyDescriptor, Marshaller>();
	private final Map<AnnotatedBinder, AnnotationBinderDefinition<?>> annotationBinders = new HashMap<AnnotatedBinder, AnnotationBinderDefinition<?>>();

	private DefinitionBuildingContext definitionBuildingContext;
	private ObjectInstantiatorFactory objectInstantiatorFactory = null;
	private Class<? extends Annotation> attributeAnnotation = null;
	private AbstractSerializerDefinition parent = null;

	@Override
	public final void configure(DefinitionBuildingContext definitionBuildingContext, ObjectInstantiatorFactory objectInstantiatorFactory) {
		// Save PropertyDescriptorFactory for later use in configure()
		this.definitionBuildingContext = definitionBuildingContext;

		// Save ObjectInstantiatorFactory for later use in configure()
		this.objectInstantiatorFactory = objectInstantiatorFactory;

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
		Iterator<ObjectObjectCursor<Type, Marshaller>> iterator = marshallerContext.getInternalMap().iterator();
		while (iterator.hasNext()) {
			ObjectObjectCursor<Type, Marshaller> entry = iterator.next();
			visitor.visitClassDefine(entry.key, entry.value);
		}

		// Visit annotated properties
		Iterator<AnnotationBinderDefinition<?>> annotationIterator = annotationBinders.values().iterator();
		while (annotationIterator.hasNext()) {
			AnnotationBinderDefinition<?> annotationBinderDefinition = annotationIterator.next();
			annotationBinderDefinition.acceptVisitor(visitor);
		}

		// Visit all property definitions
		for (Entry<PropertyDescriptor, Marshaller> entry : propertyMarshallers.entrySet()) {
			visitor.visitPropertyDescriptor(entry.getKey(), entry.getValue());
			
			Class<?> type = entry.getKey().getType();
			if (type.isPrimitive() || type.isArray() && type.getComponentType().isPrimitive()) {
				continue;
			}

			visitor.visitClassDefine(type, entry.getValue());
		}

		// Visit all children
		for (SerializerDefinition child : children) {
			child.configure(definitionBuildingContext, objectInstantiatorFactory);
			child.acceptVisitor(visitor);
		}

		// Finalize visit
		visitor.visitFinalizeSerializerDefinition(this);
	}

	protected abstract void configure();

	protected <T> ClassBinder<T> serialize(final Class<T> clazz) {
		return buildClassBinder(clazz);
	}

	protected void install(SerializerDefinition childSerializer) {
		children.add(childSerializer);
		if (childSerializer instanceof AbstractSerializerDefinition) {
			((AbstractSerializerDefinition) childSerializer).parent = this;
		}
	}

	protected void describesAttributes(Class<? extends Annotation> attributeAnnotation) {
		this.attributeAnnotation = attributeAnnotation;
	}

	protected <V> AttributeBinder<V> attribute(final String attribute) {
		return new DefinedAttributeBinder<V>() {

			@Override
			protected void setDeclaringClass(Class<?> declaringClass) {
				super.setDeclaringClass(declaringClass);
				try {
					Field reflectiveField = declaringClass.getDeclaredField(attribute);
					reflectiveField.setAccessible(true);
					property = reflectiveField;
				}
				catch (Exception e) {
					throw new SerializerDefinitionException("Property " + property + " could not be found for type " + declaringClass.getCanonicalName(), e);
				}
			}
		};
	}

	protected <V> AttributeBinder<V> attribute(final Field attribute) {
		return new DefinedAttributeBinder<V>() {

			{
				property = attribute;
				declaringClass = attribute.getDeclaringClass();
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
			public AnnotatedBinder attributes(Class<? extends Annotation> annotation) {
				return buildAnnotatedBinder(this, annotation);
			}

			@Override
			public Class<T> getType() {
				return clazz;
			}

			@Override
			@SuppressWarnings("unchecked")
			public void using(Class<?> clazz) {
				if (Marshaller.class.isAssignableFrom(clazz)) {
					try {
						using(((Class<Marshaller>) clazz).newInstance());
					}
					catch (Exception e) {
						throw new SerializerDefinitionException("Marshaller class " + clazz.getCanonicalName()
								+ " could not be instantiated. Is there a standard (public) constructor?", e);
					}
				}

			}

			@Override
			public void using(Marshaller marshaller) {
				if (marshaller instanceof AbstractObjectMarshaller) {
					marshallerContext
							.bindMarshaller(clazz, new ObjenesisDelegatingMarshaller((AbstractObjectMarshaller) marshaller, objectInstantiatorFactory));
				}
				else {
					marshallerContext.bindMarshaller(clazz, marshaller);
				}
			}

			@Override
			public void using(TypeIntrospector typeIntrospector) {
				// TODO Auto-generated method stub

			}

			@Override
			public void attributes(AttributeBinder<?>... attributes) {
				for (AttributeBinder<?> attribute : attributes) {
					if (attribute instanceof DefinedAttributeBinder) {
						DefinedAttributeBinder<?> binder = (DefinedAttributeBinder<?>) attribute;
						binder.setDeclaringClass(getType());
						binder.build();
					}
				}
			}
		};
	}

	private <T> AnnotatedBinder buildAnnotatedBinder(final ClassBinder<T> classBinder, final Class<? extends Annotation> annotation) {

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

			@Override
			public AnnotatedBinder excludes(String... properties) {
				for (String property : properties) {
					binder.addExclude(property);
				}
				return this;
			}
		};
	}

	private Class<? extends Annotation> findAttributeAnnotation(AbstractSerializerDefinition abstractSerializerDefinition) {
		if (attributeAnnotation != null) {
			return attributeAnnotation;
		}

		if (parent != null) {
			return abstractSerializerDefinition.findAttributeAnnotation(parent);
		}

		return Attribute.class;
	}

	private class AnnotationBinderDefinition<T> {

		private final AnnotatedTypeIntrospector typeIntrospector;
		private final ClassBinder<T> classBinder;
		private final List<String> excludes = new ArrayList<String>();

		private AnnotationBinderDefinition(ClassBinder<T> classBinder) {
			this.typeIntrospector = new AnnotatedTypeIntrospector(findAttributeAnnotation(AbstractSerializerDefinition.this), excludes);
			this.classBinder = classBinder;
		}

		public void addExclude(String exclude) {
			excludes.add(exclude);
		}

		public void acceptVisitor(DefinitionVisitor visitor) {
			MarshallerContext marshallers = combineMarshallers(AbstractSerializerDefinition.this);
			List<PropertyDescriptor> propertyDescriptors = typeIntrospector.introspect(classBinder.getType(),
					definitionBuildingContext.getMarshallerStrategy(), marshallers, definitionBuildingContext.getPropertyDescriptorFactory());

			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				Class<?> fieldType = propertyDescriptor.getType();
				Marshaller marshaller = propertyDescriptor.getMarshaller();
				visitor.visitAnnotatedAttribute(propertyDescriptor, marshaller);

				if (fieldType.isPrimitive() || fieldType.isArray() && fieldType.getComponentType().isPrimitive()) {
					continue;
				}

				visitor.visitClassDefine(!fieldType.isArray() ? fieldType : fieldType.getComponentType(), marshaller);
				if (marshaller == null) {
					visitFieldTypeAnnotatedProperties(!fieldType.isArray() ? fieldType : fieldType.getComponentType(), visitor);
				}
			}
		}

		@SuppressWarnings("unchecked")
		private <F> void visitFieldTypeAnnotatedProperties(Class<?> type, DefinitionVisitor visitor) {
			ClassBinder<F> classBinder = (ClassBinder<F>) buildClassBinder(type);
			new AnnotationBinderDefinition<F>(classBinder).acceptVisitor(visitor);
		}

		private MarshallerContext combineMarshallers(AbstractSerializerDefinition abstractSerializerDefinition) {
			return new InternalMarshallerContext(abstractSerializerDefinition.marshallerContext);
		}
	}

	private abstract class DefinedAttributeBinder<V> implements AttributeBinder<V> {

		protected Marshaller marshaller;
		protected Field property;
		protected Class<?> declaringClass;

		protected void setDeclaringClass(Class<?> declaringClass) {
			this.declaringClass = declaringClass;
		}

		@Override
		public AttributeBinder<V> using(Class<? extends Marshaller> marshaller) {
			try {
				using(marshaller.newInstance());
				return this;
			}
			catch (Exception e) {
				throw new SerializerDefinitionException("Marshaller class " + marshaller.getCanonicalName()
						+ " could not be instantiated. Is there a standard (public) constructor?", e);
			}
		}

		@Override
		public AttributeBinder<V> using(Marshaller marshaller) {
			if (marshaller instanceof TypeBindableMarshaller) {
				Type[] typeArguments = TypeUtil.getTypeArgument(property.getGenericType());
				this.marshaller = ((TypeBindableMarshaller) marshaller).bindType(typeArguments);
			}

			return this;
		}

		private void build() {
			if (marshaller == null) {
				marshaller = definitionBuildingContext.getMarshallerStrategy().getMarshaller(property.getType(), marshallerContext, true);
			}
			propertyMarshallers.put(definitionBuildingContext.getPropertyDescriptorFactory().byField(property, marshaller, declaringClass), marshaller);
		}
	}
}
