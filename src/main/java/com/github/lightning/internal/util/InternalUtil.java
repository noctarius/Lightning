package com.github.lightning.internal.util;

import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.util.Collection;

import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.beans.PropertyAccessorFactory;
import com.github.lightning.internal.instantiator.ObjectInstantiator;

public final class InternalUtil {

	public static final boolean UNSAFE_AVAILABLE;

	static {
		boolean unsafeAvailable = false;
		try {
			Class.forName("sun.misc.Unsafe");
			unsafeAvailable = true;
		}
		catch (Exception e) {
			// Intentionally left blank
		}

		UNSAFE_AVAILABLE = unsafeAvailable;
	}

	private InternalUtil() {
	}

	public static byte[] getChecksum(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(data, 0, data.length);
			return digest.digest();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not build checksum of data");
		}
	}

	public static byte[] getChecksum(Collection<PropertyDescriptor> propertyDescriptors) {
		return null;
	}

	public static boolean isUnsafeAvailable() {
		return UNSAFE_AVAILABLE;
	}

	@SuppressWarnings("unchecked")
	public static ObjectInstantiator buildSunUnsafeInstantiator(Class<?> type) {
		try {
			Class<? extends ObjectInstantiator> clazz = (Class<? extends ObjectInstantiator>) ClassUtil
					.loadClass("com.github.lightning.internal.instantiator.sun.SunUnsafeAllocateInstanceInstantiator");

			Constructor<? extends ObjectInstantiator> constructor = clazz.getConstructor(Class.class);
			return constructor.newInstance(type);
		}
		catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static PropertyAccessorFactory buildSunUnsafePropertyAccessor() {
		try {
			Class<? extends PropertyAccessorFactory> clazz = (Class<? extends PropertyAccessorFactory>) ClassUtil
					.loadClass("com.github.lightning.internal.beans.SunUnsafePropertyAccessorFactory");

			Constructor<? extends PropertyAccessorFactory> constructor = clazz.getConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (Exception e) {
			return null;
		}
	}
}
