package com.github.lightning.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.lightning.DefinitionVisitor;
import com.github.lightning.Marshaller;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.SerializerDefinition;
import com.github.lightning.SerializerDefinitionException;
import com.github.lightning.bindings.AnnotatedBinder;
import com.github.lightning.bindings.ClassBinder;
import com.github.lightning.bindings.MarshallerBinder;
import com.github.lightning.bindings.PropertyBinder;
import com.github.lightning.internal.beans.PropertyDescriptorFactory;

public abstract class AbstractSerializerDefinition implements SerializerDefinition {

	private final Map<Class<?>, Marshaller<?>> marshallers = new HashMap<Class<?>, Marshaller<?>>();
	private final Set<SerializerDefinition> children = new HashSet<SerializerDefinition>();
	private final Map<PropertyDescriptor, Marshaller<?>> propertyMarshallers = new HashMap<PropertyDescriptor, Marshaller<?>>();

	private Class<? extends Annotation> attributesAnnotation = null;

	@Override
	public final void acceptVisitor(DefinitionVisitor visitor) {
		// Start visiting
		visitor.visitSerializerDefinition(this);

		// Visit the attribute annotation if set
		if (attributesAnnotation != null) {
			visitor.visitAttributeAnnotation(attributesAnnotation);
		}

		// Visit all direct marshallers
		for (Entry<Class<?>, Marshaller<?>> entry : marshallers.entrySet()) {
			visitor.visitClassDefine(entry.getKey(), entry.getValue());
		}

		// Visit all property definitions
		for (Entry<PropertyDescriptor, Marshaller<?>> entry : propertyMarshallers.entrySet()) {
			visitor.visitPropertyDescriptor(entry.getKey(), entry.getValue());
		}

		// Visit all children
		for (SerializerDefinition child : children) {
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
	}

	protected <T> MarshallerBinder<T> define(final Class<T> clazz) {
		return buildMarshallerBinder(clazz);
	}

	protected void describesAttributes(Class<? extends Annotation> annotation) {
		this.attributesAnnotation = annotation;
	}

	private <T> MarshallerBinder<T> buildMarshallerBinder(final Class<T> clazz) {
		return new MarshallerBinder<T>() {

			@Override
			public void byMarshaller(Class<? extends Marshaller<T>> marshaller) {
				try {
					byMarshaller(marshaller.newInstance());
				}
				catch (Exception e) {
					throw new SerializerDefinitionException("Marshaller class " + marshaller.getCanonicalName()
							+ " could not be instantiated. Is there a standard (public) constructor?");
				}
			}

			@Override
			public void byMarshaller(Marshaller<T> marshaller) {
				marshallers.put(clazz, marshaller);
			}
		};
	}

	private <T> ClassBinder<T> buildClassBinder(final Class<T> clazz) {
		return new ClassBinder<T>() {

			@Override
			public AnnotatedBinder attributes() {
				return buildAnnotatedBinder(this, attributesAnnotation);
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
							+ clazz.getCanonicalName());
				}
			}

			@Override
			public <V> PropertyBinder<V> property(Field property) {
				return buildPropertyBinder(this, property);
			}
		};
	}

	private <T> AnnotatedBinder buildAnnotatedBinder(final ClassBinder<T> classBinder,
			final Class<? extends Annotation> annotation) {

		return new AnnotatedBinder() {

			@Override
			public AnnotatedBinder exclude(String property) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	private <T, V> PropertyBinder<V> buildPropertyBinder(final ClassBinder<T> classBinder, final Field property) {
		return new PropertyBinder<V>() {

			@Override
			public void byMarshaller(Class<? extends Marshaller<?>> marshaller) {
				try {
					byMarshaller(marshaller.newInstance());
				}
				catch (Exception e) {
					throw new SerializerDefinitionException("Marshaller class " + marshaller.getCanonicalName()
							+ " could not be instantiated. Is there a standard (public) constructor?");
				}
			}

			@Override
			public void byMarshaller(Marshaller<?> marshaller) {
				propertyMarshallers.put(PropertyDescriptorFactory.byField(property, marshaller), marshaller);
			}
		};
	}
}
