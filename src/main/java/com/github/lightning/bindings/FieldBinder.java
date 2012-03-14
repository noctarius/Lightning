package com.github.lightning.bindings;

import com.github.lightning.Marshaller;

public interface FieldBinder {

	void byMarshaller(Class<? extends Marshaller> marshaller);

}
