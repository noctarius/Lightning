package com.github.lightning.internal.instantiator.sun;

import java.lang.reflect.Field;

import com.github.lightning.internal.instantiator.ObjectInstantiator;

public class SunUnsafeAllocateInstanceInstantiator implements ObjectInstantiator {

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

	protected final Class<?> type;

	public SunUnsafeAllocateInstanceInstantiator(Class<?> type) {
		this.type = type;
	}

	@Override
	public Object newInstance() {
		try {
			if (UNSAFE != null)
				return UNSAFE.allocateInstance(type);
		}
		catch (Exception e) {
			// ignore and return null
		}

		return null;
	}

}
