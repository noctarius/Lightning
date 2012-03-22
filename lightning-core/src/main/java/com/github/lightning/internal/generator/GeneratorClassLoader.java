package com.github.lightning.internal.generator;

public class GeneratorClassLoader extends ClassLoader {

	public GeneratorClassLoader(final ClassLoader classLoader) {
		super(classLoader);
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> loadClass(final byte[] data) {
		return (Class<T>) defineClass(null, data, 0, data.length);
	}
}
