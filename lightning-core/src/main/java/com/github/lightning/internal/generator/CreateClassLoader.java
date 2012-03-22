package com.github.lightning.internal.generator;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class CreateClassLoader implements PrivilegedAction<GeneratorClassLoader> {

	private final ClassLoader classLoader;

	public CreateClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public GeneratorClassLoader run() {
		return new GeneratorClassLoader(classLoader);
	}

	public static final GeneratorClassLoader createClassLoader(ClassLoader classLoader) {
		return AccessController.doPrivileged(new CreateClassLoader(classLoader));
	}
}
