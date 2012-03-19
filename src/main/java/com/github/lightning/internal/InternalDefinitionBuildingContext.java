package com.github.lightning.internal;

import com.github.lightning.DefinitionBuildingContext;
import com.github.lightning.MarshallerStrategy;
import com.github.lightning.PropertyDescriptorFactory;

public class InternalDefinitionBuildingContext implements DefinitionBuildingContext {

	private final PropertyDescriptorFactory propertyDescriptorFactory;
	private final MarshallerStrategy marshallerStrategy;

	public InternalDefinitionBuildingContext(MarshallerStrategy marshallerStrategy, PropertyDescriptorFactory propertyDescriptorFactory) {
		this.marshallerStrategy = marshallerStrategy;
		this.propertyDescriptorFactory = propertyDescriptorFactory;
	}

	@Override
	public PropertyDescriptorFactory getPropertyDescriptorFactory() {
		return propertyDescriptorFactory;
	}

	@Override
	public MarshallerStrategy getMarshallerStrategy() {
		return marshallerStrategy;
	}
}
