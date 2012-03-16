package com.github.lightning.internal.util;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

import com.github.lightning.PropertyAccessor;

public final class BeanUtil {

	private BeanUtil() {
	}

	public static String buildPropertyName(Method method) {
		return buildPropertyName(method.getName());
	}

	public static String buildPropertyName(String methodName) {
		return StringUtil.toLowerCamelCase(extractPropertyName(methodName));
	}

	public static <T> String buildInternalSignature(String propertyName, PropertyAccessor<T> propertyAccessor) {
		String type = Type.getDescriptor(propertyAccessor.getType());
		return new StringBuilder("(").append(propertyName).append(")::").append(type).toString();
	}

	private static String extractPropertyName(String methodName) {
		if (methodName.toUpperCase().startsWith("GET") || methodName.toUpperCase().startsWith("IS") || methodName.toUpperCase().startsWith("SET")) {
			char[] characters = methodName.toCharArray();
			for (int i = 1; i < characters.length; i++) {
				if (Character.isUpperCase(characters[i])) {
					return StringUtil.toLowerCamelCase(methodName.substring(i));
				}
			}
		}
		return StringUtil.toLowerCamelCase(methodName);
	}

}
