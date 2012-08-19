package com.github.lightning.base;

import com.github.lightning.metadata.PropertyDescriptor;
import com.github.lightning.metadata.ValueNullableEvaluator;

public class DefaultValueNullableEvaluator implements ValueNullableEvaluator {

	@Override
	public boolean isValueNullable(PropertyDescriptor propertyDescriptor) {
		return !propertyDescriptor.getType().isPrimitive();
	}

}
