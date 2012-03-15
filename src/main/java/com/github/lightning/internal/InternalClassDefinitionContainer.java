package com.github.lightning.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.internal.bundle.cern.colt.map.AbstractLongObjectMap;
import com.github.lightning.internal.bundle.cern.colt.map.OpenLongObjectHashMap;

class InternalClassDefinitionContainer implements ClassDefinitionContainer {

	private final List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
	private final AbstractLongObjectMap<ClassDefinition> classDefinitionsMappings;

	InternalClassDefinitionContainer(List<ClassDefinition> classDefinitions) {
		this.classDefinitions.addAll(classDefinitions);
		classDefinitionsMappings = new OpenLongObjectHashMap<ClassDefinition>(ClassDefinition.class, classDefinitions.size());
	}

	@Override
	public Collection<ClassDefinition> getClassDefinitions() {
		return Collections.unmodifiableCollection(classDefinitions);
	}

	@Override
	public Class<?> getClassById(long id) {
		ClassDefinition classDefinition = classDefinitionsMappings.get(id);
		return classDefinition != null ? classDefinition.getType() : null;
	}

}
