package com.github.lightning;

public interface SerializerDefinition {

	void configure(DefinitionBuildingContext definitionBuildingContext);

	void acceptVisitor(DefinitionVisitor visitor);

}
