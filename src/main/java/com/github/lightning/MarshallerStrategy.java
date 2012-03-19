package com.github.lightning;

import java.util.Map;

public interface MarshallerStrategy {

	Marshaller getMarshaller(Class<?> type, Map<Class<?>, Marshaller> definedMarshallers);

}
