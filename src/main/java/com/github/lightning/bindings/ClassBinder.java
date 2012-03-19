package com.github.lightning.bindings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface ClassBinder<T> {

	AnnotatedBinder attributes();

	AnnotatedBinder with(Class<? extends Annotation> annotation);

	<V> PropertyBinder<V> property(String property);

	<V> PropertyBinder<V> property(Field property);

	Class<T> getType();

}
