package com.github.lightning;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface PropertyDescriptorFactory {

	PropertyDescriptor byMethod(Method method, Marshaller marshaller);

	PropertyDescriptor byField(Field field, Marshaller marshaller);

}
