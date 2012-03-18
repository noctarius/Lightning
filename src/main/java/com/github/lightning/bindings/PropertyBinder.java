package com.github.lightning.bindings;

import com.github.lightning.Marshaller;

public interface PropertyBinder<T> {

	void byMarshaller(Class<? extends Marshaller<?>> marshaller);

	void byMarshaller(Marshaller<?> marshaller);

}
