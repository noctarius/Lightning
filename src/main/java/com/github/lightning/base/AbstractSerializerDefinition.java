package com.github.lightning.base;

import java.lang.annotation.Annotation;

import com.github.lightning.DefinitionVisitor;
import com.github.lightning.SerializerDefinition;
import com.github.lightning.bindings.ClassBinder;
import com.github.lightning.bindings.MarshallerBinder;

public abstract class AbstractSerializerDefinition implements SerializerDefinition {

	@Override
	public final void visitDefinition(DefinitionVisitor visitor) {
		// TODO implementation missing
	}

	protected abstract void configure();

	protected ClassBinder bind(Class<?> clazz) {
		// TODO implementation missing
		return null;
	}

	protected void install(Class<? extends SerializerDefinition> childSerializer) {
		// TODO implementation missing
	}

	protected MarshallerBinder define(Class<?> clazz) {
		// TODO implementation missing
		return null;
	}

	protected void describesAttributes(Class<? extends Annotation> annotation) {
		// TODO implementation missing
	}

}
