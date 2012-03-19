package com.github.lightning.bindings;

import com.github.lightning.Marshaller;

public interface PropertyBinder<V> {

	void byMarshaller(Class<? extends Marshaller> marshaller);

	void byMarshaller(Marshaller marshaller);

}
