/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lightning.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.objectweb.asm.ClassVisitor;

import com.github.lightning.logging.LoggerAdapter;
import com.github.lightning.metadata.ClassDefinition;
import com.github.lightning.metadata.ClassDefinitionContainer;
import com.github.lightning.metadata.PropertyDescriptor;

public class ClassDefinitionContainerTestCase {

	private static final Class<?>[] CLASSES = { ClassVisitor.class };

	@Test
	public void testClassDefinitionContainer() throws Exception {
		final Set<ClassDefinition> classDefinitions = new HashSet<ClassDefinition>();

		for (Class<?> clazz : CLASSES) {
			PropertyDescriptor label = null;
			classDefinitions.add(new InternalClassDefinition(clazz, Collections.<PropertyDescriptor> emptyList(), new LoggerAdapter()));
		}

		ClassDefinitionContainer classDefinitionContainer = new InternalClassDefinitionContainer(classDefinitions);

		for (ClassDefinition classDefinition : classDefinitionContainer.getClassDefinitions()) {
			Class<?> clazz = null;
		}
	}
}
