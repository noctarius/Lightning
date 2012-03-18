package com.github.lightning.bindings;

import com.github.lightning.Marshaller;

public interface MarshallerBinder<T> {

	void byMarshaller(Class<? extends Marshaller<T>> marshaller);

	void byMarshaller(Marshaller<T> marshaller);

}
