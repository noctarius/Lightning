package com.github.lightning.internal;

import com.github.lightning.Serializer;
import com.github.lightning.metadata.ClassDescriptor;

public interface ClassDescriptorAwareSerializer extends Serializer {

	ClassDescriptor findClassDescriptor(Class<?> type);

}
