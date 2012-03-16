package com.github.lightning.internal.util;

import java.lang.reflect.Field;

@SuppressWarnings("restriction")
public final class UnsafeUtil {

	private static final sun.misc.Unsafe UNSAFE;

	static {
		sun.misc.Unsafe unsafe;
		try {
			Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			unsafe = (sun.misc.Unsafe) unsafeField.get(null);
		}
		catch (Exception e) {
			unsafe = null;
		}

		UNSAFE = unsafe;
	}

	private UnsafeUtil() {
	}

	public static sun.misc.Unsafe getUnsafe() {
		return UNSAFE;
	}
}
