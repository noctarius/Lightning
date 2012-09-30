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
package org.apache.directmemory.lightning.internal.beans;

import java.lang.reflect.Field;

import org.apache.directmemory.lightning.metadata.AccessorType;
import org.apache.directmemory.lightning.metadata.ArrayPropertyAccessor;

public abstract class FieldArrayPropertyAccessor
    extends FieldValuePropertyAccessor
    implements ArrayPropertyAccessor
{

    private final Field field;

    private final Class<?> definedClass;

    protected FieldArrayPropertyAccessor( Field field, Class<?> definedClass )
    {
        super( field, definedClass );
        this.field = field;
        this.definedClass = definedClass;
    }

    @Override
    public boolean isArrayType()
    {
        return true;
    }

    @Override
    public Class<?> getDefinedClass()
    {
        return definedClass;
    }

    @Override
    public AccessorType getAccessorType()
    {
        return AccessorType.Field;
    }

    @Override
    public Class<?> getType()
    {
        return field.getType();
    }

    @Override
    protected Field getField()
    {
        return field;
    }
}
