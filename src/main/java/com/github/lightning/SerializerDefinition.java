package com.github.lightning;

public interface SerializerDefinition {

	void configure(PropertyDescriptorFactory propertyDescriptorFactory);

	void acceptVisitor(DefinitionVisitor visitor);

}
