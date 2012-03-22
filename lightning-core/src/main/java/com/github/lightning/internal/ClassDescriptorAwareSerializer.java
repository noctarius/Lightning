package com.github.lightning.internal;

import com.github.lightning.ClassDescriptor;
import com.github.lightning.Serializer;

public interface ClassDescriptorAwareSerializer extends Serializer {

	ClassDescriptor findClassDescriptor(Class<?> type);

}
