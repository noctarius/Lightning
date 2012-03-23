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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public final class ClassUtil {

	private static final Map<Class<?>, Long> SERIAL_VERSION_UID_CACHE = new ConcurrentHashMap<Class<?>, Long>();

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

	@SuppressWarnings("unchecked")
	public static long calculateSerialVersionUID(Class<?> clazz) {
		Long serialVersionUID = SERIAL_VERSION_UID_CACHE.get(clazz);
		if (serialVersionUID != null) {
			return serialVersionUID;
		}

		if (Serializable.class.isAssignableFrom(clazz)) {
			serialVersionUID = ObjectStreamClass.lookup(clazz).getSerialVersionUID();
			SERIAL_VERSION_UID_CACHE.put(clazz, serialVersionUID);
			return serialVersionUID;
		}

		serialVersionUID = getSerialVersionUIDFromField(clazz);
		if (serialVersionUID != null) {
			SERIAL_VERSION_UID_CACHE.put(clazz, serialVersionUID);
			return serialVersionUID;
		}

		try {
			ClassReader reader = new ClassReader(Type.getInternalName(clazz).replace("/", "."));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);

			ClassNode classNode = new ClassNode();
			reader.accept(classNode, 0);

			// Classname
			out.writeUTF(toJavaName(classNode.name));

			// Modifiers
			out.writeInt(clazz.getModifiers() & (Modifier.PUBLIC | Modifier.FINAL | Modifier.INTERFACE | Modifier.ABSTRACT));

			// Interfaces
			Collections.sort(classNode.interfaces);
			for (int i = 0; i < classNode.interfaces.size(); i++) {
				out.writeUTF(toJavaName((String) classNode.interfaces.get(i)));
			}

			// Fields
			Field[] fields = clazz.getDeclaredFields();
			Arrays.sort(fields, new Comparator<Field>() {

				@Override
				public int compare(Field o1, Field o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			for (Field field : fields) {
				int mods = field.getModifiers();
				if (((mods & Modifier.PRIVATE) == 0 || (mods & (Modifier.STATIC | Modifier.TRANSIENT)) == 0)) {
					out.writeUTF(field.getName());
					out.writeInt(mods);
					out.writeUTF(Type.getDescriptor(field.getType()));
				}
			}

			// Static Initializer
			if (getStaticInitializer(classNode) != null) {
				out.writeUTF("<clinit>");
				out.writeInt(Modifier.STATIC);
				out.writeUTF("()V");
			}

			// Constructors
			Constructor<?>[] constructors = clazz.getDeclaredConstructors();
			Arrays.sort(constructors, new Comparator<Constructor<?>>() {

				@Override
				public int compare(Constructor<?> o1, Constructor<?> o2) {
					return Type.getConstructorDescriptor(o1).compareTo(Type.getConstructorDescriptor(o2));
				}
			});

			for (int i = 0; i < constructors.length; i++) {
				Constructor<?> constructor = constructors[i];
				int mods = constructor.getModifiers();
				if ((mods & Modifier.PRIVATE) == 0) {
					out.writeUTF("<init>");
					out.writeInt(mods);
					out.writeUTF(toJavaName(Type.getConstructorDescriptor(constructor)));
				}
			}

			// Methods
			Method[] methods = clazz.getDeclaredMethods();
			Arrays.sort(methods, new Comparator<Method>() {

				@Override
				public int compare(Method o1, Method o2) {
					return Type.getMethodDescriptor(o1).compareTo(Type.getMethodDescriptor(o2));
				}
			});

			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				int mods = method.getModifiers();
				if ((mods & Modifier.PRIVATE) == 0) {
					out.writeUTF("<init>");
					out.writeInt(mods);
					out.writeUTF(toJavaName(Type.getMethodDescriptor(method)));
				}
			}

			// Final calculation
			out.flush();
			MessageDigest digest = MessageDigest.getInstance("SHA");
			byte[] checksum = digest.digest(baos.toByteArray());

			long hash = 0;
			for (int i = Math.min(checksum.length, 8) - 1; i >= 0; i--) {
				hash = (hash << 8) | (checksum[i] & 0xFF);
			}

			SERIAL_VERSION_UID_CACHE.put(clazz, hash);
			return hash;
		}
		catch (IOException e) {
		}
		catch (NoSuchAlgorithmException e) {
		}

		return -1L;
	}

	public static byte[] getClassBytes(Class<?> clazz) {
		try {
			ClassLoader classLoader = clazz.getClassLoader();
			if (classLoader == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
			}

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

	private static String toJavaName(String classname) {
		return classname.replace("/", ".");
	}

	private static Long getSerialVersionUIDFromField(Class<?> clazz) {
		try {
			Field f = clazz.getDeclaredField("serialVersionUID");
			int mask = Modifier.STATIC | Modifier.FINAL;
			if ((f.getModifiers() & mask) == mask) {
				f.setAccessible(true);
				return Long.valueOf(f.getLong(null));
			}
		}
		catch (Exception ex) {
		}
		return null;
	}

	private static MethodNode getStaticInitializer(ClassNode classNode) {
		for (Object method : classNode.methods) {
			MethodNode methodNode = (MethodNode) method;
			if ("<clinit>".equals(methodNode.name) && ((methodNode.access & Opcodes.ACC_STATIC) != 0)) {
				return methodNode;
			}
		}
		return null;
	}
}
