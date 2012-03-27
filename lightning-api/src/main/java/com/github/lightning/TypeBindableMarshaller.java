package com.github.lightning;

import java.lang.reflect.Field;

public interface TypeBindableMarshaller {

	Marshaller bindType(Field property);

}
