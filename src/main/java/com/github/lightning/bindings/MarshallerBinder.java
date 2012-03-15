package com.github.lightning.bindings;

import com.github.lightning.Marshaller;

public interface MarshallerBinder {

	void byMarshaller(Class<? extends Marshaller> marshaller);

}
