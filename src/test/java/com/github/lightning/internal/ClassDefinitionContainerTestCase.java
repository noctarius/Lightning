package com.github.lightning.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.objectweb.asm.tree.LabelNode;

import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.logging.LoggerAdapter;

public class ClassDefinitionContainerTestCase {

	private static final Class<?>[] CLASSES = { LabelNode.class };

	@Test
	public void testClassDefinitionContainer() throws Exception {
		final List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();

		for (Class<?> clazz : CLASSES) {
			PropertyDescriptor label = null;
			classDefinitions.add(new InternalClassDefinition(clazz, Collections.<PropertyDescriptor> emptyList(),
					new LoggerAdapter()));
		}

		ClassDefinitionContainer classDefinitionContainer = new InternalClassDefinitionContainer(classDefinitions);

		for (ClassDefinition classDefinition : classDefinitionContainer.getClassDefinitions()) {
			Class<?> clazz = null;
		}
	}
}
