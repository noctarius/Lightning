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
import java.lang.reflect.Method;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.generator.PropertyDescriptorFactory;
import org.apache.directmemory.lightning.internal.util.BeanUtil;
import org.apache.directmemory.lightning.logging.Logger;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;

public class InternalPropertyDescriptorFactory
    implements PropertyDescriptorFactory
{

    private final PropertyAccessorStrategy propertyAccessorStrategy;

    public InternalPropertyDescriptorFactory( Logger logger )
    {
        propertyAccessorStrategy = new PropertyAccessorStrategy( logger );
    }

    @Override
    public PropertyDescriptor byMethod( Method method, Marshaller marshaller, Class<?> definedClass )
    {
        PropertyAccessor propertyAccessor = propertyAccessorStrategy.byMethod( method, definedClass );
        String propertyName = BeanUtil.buildPropertyName( method );
        return new InternalPropertyDescriptor( propertyName, marshaller, method.getAnnotations(), propertyAccessor );
    }

    @Override
    public PropertyDescriptor byField( Field field, Marshaller marshaller, Class<?> definedClass )
    {
        PropertyAccessor propertyAccessor = propertyAccessorStrategy.byField( field, definedClass );
        return new InternalPropertyDescriptor( field.getName(), marshaller, field.getAnnotations(), propertyAccessor );
    }
}
