package com.github.lightning.internal.util;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.Type;

public final class ClassUtil {

	private ClassUtil() {
	}

	public static byte[] getClassBytes(Class<?> clazz) {
		try {
			ClassLoader classLoader = clazz.getClassLoader();
			String internalName = Type.getInternalName(clazz);
			InputStream stream = classLoader.getResourceAsStream(internalName + "class");
			byte[] data = new byte[stream.available()];
			stream.read(data);
			return data;
		}
		catch (IOException e) {
			throw new RuntimeException("Class bytes could not be read", e);
		}
	}

}
