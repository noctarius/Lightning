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

			Marshaller marshaller = marshallerStrategy.getMarshaller(fieldType, marshallerContext);

			if (marshaller == null && fieldType.isArray()) {
				marshaller = marshallerStrategy.getMarshaller(fieldType.getComponentType(), marshallerContext);
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
