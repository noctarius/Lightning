package com.github.lightning.internal.generator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.lightning.ClassDescriptor;
import com.github.lightning.Marshaller;
import com.github.lightning.ObjectInstantiator;
import com.github.lightning.PropertyAccessor;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.SerializerDefinitionException;
import com.github.lightning.internal.ClassDescriptorAwareSerializer;
import com.github.lightning.internal.instantiator.ObjenesisSerializer;

abstract class AbstractGeneratedMarshaller implements Marshaller {

	private final Class<?> marshalledType;
	private final Map<Class<?>, Marshaller> marshallers;
	private final ClassDescriptor classDescriptor;
	private final List<PropertyDescriptor> propertyDescriptors;
	private final ObjectInstantiator objectInstantiator;

	AbstractGeneratedMarshaller(Class<?> marshalledType, Map<Class<?>, Marshaller> marshallers,
			ClassDescriptorAwareSerializer serializer, ObjenesisSerializer objenesisSerializer) {

		this.marshalledType = marshalledType;
		this.marshallers = marshallers;
		this.classDescriptor = serializer.findClassDescriptor(marshalledType);
		this.propertyDescriptors = Collections.unmodifiableList(classDescriptor.getPropertyDescriptors());
		this.objectInstantiator = objenesisSerializer.getInstantiatorOf(marshalledType);
	}

	@Override
	public boolean acceptType(Class<?> type) {
		return marshalledType.isAssignableFrom(type);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
		V value = (V) objectInstantiator.newInstance();
		return unmarshall(value, type, dataInput);
	}

	protected abstract <V> V unmarshall(V value, Class<?> type, DataInput dataInput);
	
	protected ClassDescriptor getClassDescriptor() {
		return classDescriptor;
	}

	protected Object newInstance() {
		return objectInstantiator.newInstance();
	}

	protected PropertyDescriptor getPropertyDescriptor(String propertyName) {
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getPropertyName().equals(propertyName)) {
				return propertyDescriptor;
			}
		}

		// This should never happen
		return null;
	}

	protected PropertyAccessor getPropertyAccessor(String propertyName) {
		return getPropertyDescriptor(propertyName).getPropertyAccessor();
	}

	protected Marshaller findMarshaller(Class<?> type) {
		Marshaller marshaller = marshallers.get(type);
		if (marshaller != null) {
			return marshaller;
		}

		return new DelegatingMarshaller(type);
	}

	private class DelegatingMarshaller implements Marshaller {

		private final Class<?> type;
		private Marshaller marshaller;

		private DelegatingMarshaller(Class<?> type) {
			this.type = type;
		}

		@Override
		public boolean acceptType(Class<?> type) {
			return this.type.isAssignableFrom(type);
		}

		@Override
		public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
			Marshaller marshaller = this.marshaller;
			if (marshaller == null) {
				marshaller = getMarshaller();
			}

			if (marshaller == null) {
				throw new SerializerDefinitionException("No marshaller for type " + type + " found");
			}

			marshaller.marshall(value, type, dataOutput);
		}

		@Override
		public <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
			Marshaller marshaller = this.marshaller;
			if (marshaller == null) {
				marshaller = getMarshaller();
			}

			if (marshaller == null) {
				throw new SerializerDefinitionException("No marshaller for type " + type + " found");
			}

			return marshaller.unmarshall(type, dataInput);
		}

		private synchronized Marshaller getMarshaller() {
			if (marshaller == null) {
				marshaller = findMarshaller(type);
			}
			return marshaller;
		}

	}
}