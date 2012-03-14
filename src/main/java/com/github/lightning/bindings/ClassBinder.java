package com.github.lightning.bindings;

import java.lang.annotation.Annotation;

public interface ClassBinder {

	AnnotatedBinder attributes();

	AnnotatedBinder with(Class<? extends Annotation> annotation);

	FieldBinder field(String field);

}
