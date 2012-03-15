package com.github.lightning;

import java.util.Collection;

public interface ClassDefinitionContainer {

	Collection<ClassDefinition> getClassDefinitions();

	Class<?> getClassById(long id);

}
