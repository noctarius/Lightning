package com.github.lightning.internal.util;

import java.util.HashMap;
import java.util.Map;

import com.github.lightning.Marshaller;

public class MarshallerUtil {

	public static final Map<Class<?>, Marshaller> BASE_MARSHALLER = new HashMap<Class<?>, Marshaller>();

	static {
		// TODO
	}

	private MarshallerUtil() {
	}

	public static Marshaller getBestMatchingMarshaller(Class<?> type, Map<Class<?>, Marshaller> marshallers) {
		return BASE_MARSHALLER.get(type);
	}
}
