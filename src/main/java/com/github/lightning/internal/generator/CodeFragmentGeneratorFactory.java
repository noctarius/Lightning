package com.github.lightning.internal.generator;

import com.github.lightning.IllegalAccessorException;
import com.github.lightning.PropertyAccessor.AccessorType;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.CodeFragmentGenerator;

public final class CodeFragmentGeneratorFactory {

	private CodeFragmentGeneratorFactory() {
	}

	public static CodeFragmentGenerator getCodeFragmentGenerator(PropertyDescriptor propertyDescriptor, AccessType accessType) {
		if (propertyDescriptor.getPropertyAccessor().getAccessorType() == AccessorType.Field) {
			if (accessType == AccessType.Read) {
				return new ReadPropertyGenerator(propertyDescriptor);
			}

			return new WritePropertyGenerator(propertyDescriptor);
		}

		throw new IllegalAccessorException("No Bytecode Generator found for given strategy");
	}

	public static enum AccessType {
		Read,
		Write
	}
}
