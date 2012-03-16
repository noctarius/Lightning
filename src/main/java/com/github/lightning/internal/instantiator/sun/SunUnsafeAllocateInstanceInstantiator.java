package com.github.lightning.internal.instantiator.sun;

import com.github.lightning.internal.instantiator.ObjectInstantiator;
import com.github.lightning.internal.util.UnsafeUtil;

@SuppressWarnings("restriction")
public class SunUnsafeAllocateInstanceInstantiator implements ObjectInstantiator {

	private static final sun.misc.Unsafe UNSAFE = UnsafeUtil.getUnsafe();

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
