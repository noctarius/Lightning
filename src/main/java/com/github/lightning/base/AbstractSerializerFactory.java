package com.github.lightning.base;

import com.github.lightning.SerializerFactory;
import com.github.lightning.bindings.ClassBinder;

public abstract class AbstractSerializerFactory implements SerializerFactory {

	protected ClassBinder bind(Class<?> clazz) {
		return null;
	}

	protected void install(Class<? extends SerializerFactory> childSerializer) {

	}

}
