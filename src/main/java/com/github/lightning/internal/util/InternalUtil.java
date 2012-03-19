package com.github.lightning.internal.util;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.lightning.ObjectInstantiator;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.beans.PropertyAccessorFactory;
import com.github.lightning.logging.Logger;

public final class InternalUtil {

	public static final Charset CHARSET = Charset.forName("UTF-8");
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

	public static byte[] getChecksum(byte[] data, Logger logger) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(data, 0, data.length);
			return digest.digest();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not build checksum of data");
		}
	}

	public static byte[] getChecksum(Collection<PropertyDescriptor> propertyDescriptors, Logger logger) {
		final StringBuilder builder = new StringBuilder();

		// Clone and sort list of PropertyDescriptors
		List<PropertyDescriptor> temp = new ArrayList<PropertyDescriptor>(propertyDescriptors);
		Collections.sort(temp);

		for (PropertyDescriptor propertyDescriptor : temp) {
			logger.trace("Adding property " + propertyDescriptor.getName() + " to checksum");
			builder.append(propertyDescriptor.getInternalSignature());
		}

		return getChecksum(builder.toString().getBytes(CHARSET), logger);
	}

	public static boolean isUnsafeAvailable() {
		return UNSAFE_AVAILABLE;
	}

	@SuppressWarnings("unchecked")
	public static ObjectInstantiator buildSunUnsafeInstantiator(Class<?> type) {
		try {
			Class<? extends ObjectInstantiator> clazz = (Class<? extends ObjectInstantiator>) ClassUtil
					.loadClass("com.github.lightning.internal.instantiator.sun.SunUnsafeAllocateInstanceInstantiator");

			Constructor<? extends ObjectInstantiator> constructor = clazz.getDeclaredConstructor(Class.class);
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

			Constructor<? extends PropertyAccessorFactory> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (Exception e) {
			return null;
		}
	}
}
