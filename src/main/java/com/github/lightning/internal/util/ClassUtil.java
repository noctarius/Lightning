/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lightning.internal.util;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.Type;

public final class ClassUtil {

	private ClassUtil() {
	}

	public static Class<?> loadClass(String canonicalName) throws ClassNotFoundException {
		return loadClass(canonicalName, ClassUtil.class.getClassLoader());
	}

	public static Class<?> loadClass(String canonicalName, ClassLoader classLoader) throws ClassNotFoundException {
		Class<?> type = null;
		try {
			type = classLoader.loadClass(canonicalName);
		}
		catch (ClassNotFoundException e) {
			// Intentionally left blank
		}

		if (type == null) {
			try {
				type = Class.forName(canonicalName);
			}
			catch (ClassNotFoundException e) {
				// Intentionally left blank
			}
		}

		if (type == null) {
			try {
				ClassLoader tcl = Thread.currentThread().getContextClassLoader();
				type = tcl.loadClass(canonicalName);
			}
			catch (ClassNotFoundException e) {
				// Intentionally left blank
			}
		}

		if (type == null) {
			try {
				ClassLoader ccl = ClassUtil.class.getClassLoader();
				type = ccl.loadClass(canonicalName);
			}
			catch (ClassNotFoundException e) {
				// Intentionally left blank
			}
		}

		if (type != null) {
			return type;
		}

		throw new ClassNotFoundException("Class " + canonicalName + " not found on classpath");
	}

	public static byte[] getClassBytes(Class<?> clazz) {
		try {
			ClassLoader classLoader = clazz.getClassLoader();
			String internalName = Type.getInternalName(clazz);
			InputStream stream = classLoader.getResourceAsStream(internalName + ".class");
			byte[] data = new byte[stream.available()];
			stream.read(data);
			return data;
		}
		catch (IOException e) {
			throw new RuntimeException("Class bytes could not be read", e);
		}
	}

}
