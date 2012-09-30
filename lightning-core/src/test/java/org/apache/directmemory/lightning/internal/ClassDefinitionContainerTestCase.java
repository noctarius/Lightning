/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directmemory.lightning.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.directmemory.lightning.internal.InternalClassDefinition;
import org.apache.directmemory.lightning.internal.InternalClassDefinitionContainer;
import org.apache.directmemory.lightning.logging.LoggerAdapter;
import org.apache.directmemory.lightning.metadata.ClassDefinition;
import org.apache.directmemory.lightning.metadata.ClassDefinitionContainer;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;

public class ClassDefinitionContainerTestCase
{

    private static final Class<?>[] CLASSES = { ClassVisitor.class };

    @Test
    public void testClassDefinitionContainer()
        throws Exception
    {
        final Set<ClassDefinition> classDefinitions = new HashSet<ClassDefinition>();

        for ( Class<?> clazz : CLASSES )
        {
            PropertyDescriptor label = null;
            classDefinitions.add( new InternalClassDefinition( clazz, Collections.<PropertyDescriptor> emptyList(),
                                                               new LoggerAdapter() ) );
        }

        ClassDefinitionContainer classDefinitionContainer = new InternalClassDefinitionContainer( classDefinitions );

        for ( ClassDefinition classDefinition : classDefinitionContainer.getClassDefinitions() )
        {
            Class<?> clazz = null;
        }
    }
}
