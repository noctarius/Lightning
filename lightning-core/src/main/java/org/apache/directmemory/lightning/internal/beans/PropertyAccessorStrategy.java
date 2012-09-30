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

import org.apache.directmemory.lightning.internal.util.InternalUtil;
import org.apache.directmemory.lightning.logging.Logger;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;

public class PropertyAccessorStrategy
{

    private final PropertyAccessorFactory reflectionPropertyAccessorFactory = new ReflectionPropertyAccessorFactory();

    private final PropertyAccessorFactory reflectASMPropertyAccessorFactory = new ReflectASMPropertyAccessorFactory();

    private final PropertyAccessorFactory sunUnsafePropertyAccessorFactory;

    private final Logger logger;

    PropertyAccessorStrategy( Logger logger )
    {
        this.logger = logger.getChildLogger( getClass() );

        PropertyAccessorFactory factory = null;
        if ( InternalUtil.isUnsafeAvailable() )
        {
            factory = InternalUtil.buildSunUnsafePropertyAccessor();
            this.logger.trace( "Found sun.misc.Unsafe" );
        }
        sunUnsafePropertyAccessorFactory = factory;
    }

    PropertyAccessor byField( Field field, Class<?> definedClass )
    {
        PropertyAccessor propertyAccessor = null;
        if ( sunUnsafePropertyAccessorFactory != null )
        {
            propertyAccessor = sunUnsafePropertyAccessorFactory.fieldAccess( field, definedClass );
        }

        if ( propertyAccessor == null )
        {
            propertyAccessor = reflectASMPropertyAccessorFactory.fieldAccess( field, definedClass );
        }

        if ( propertyAccessor != null )
        {
            return propertyAccessor;
        }

        return reflectionPropertyAccessorFactory.fieldAccess( field, definedClass );
    }

    PropertyAccessor byMethod( Method method, Class<?> definedClass )
    {
        PropertyAccessor propertyAccessor = reflectASMPropertyAccessorFactory.methodAccess( method, definedClass );
        if ( propertyAccessor != null )
        {
            return propertyAccessor;
        }

        return reflectionPropertyAccessorFactory.methodAccess( method, definedClass );
    }
}
