package com.github.lightning.internal.util;

import java.security.MessageDigest;
import java.util.Collection;

import com.github.lightning.PropertyDescriptor;

public final class InternalUtil {

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

}
