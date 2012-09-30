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
import java.util.HashMap;
import java.util.Map;

import org.apache.directmemory.lightning.internal.util.BeanUtil;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;

public class ReflectASMPropertyAccessorFactory
    implements PropertyAccessorFactory
{

    private final Map<Class<?>, MethodAccess> methodAccessCache = new HashMap<Class<?>, MethodAccess>();

    private final Map<Class<?>, FieldAccess> fieldAccessCache = new HashMap<Class<?>, FieldAccess>();

    @Override
    public PropertyAccessor fieldAccess( Field field, Class<?> definedClass )
    {
        if ( field.getType().isArray() )
        {
            return null;
        }

        try
        {
            return buildForField( field, definedClass );
        }
        catch ( IllegalArgumentException e )
        {
            // If field is not public
            return null;
        }
    }

    @Override
    public PropertyAccessor methodAccess( Method method, Class<?> definedClass )
    {
        if ( method.getReturnType().isArray() )
        {
            return null;
        }

        try
        {
            return buildForMethod( method, definedClass );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }

    private FieldAccess getFieldAccess( Field field )
    {
        Class<?> declaringClass = field.getDeclaringClass();

        FieldAccess fieldAccess = fieldAccessCache.get( declaringClass );
        if ( fieldAccess != null )
        {
            return fieldAccess;
        }

        fieldAccess = FieldAccess.get( declaringClass );
        fieldAccessCache.put( declaringClass, fieldAccess );

        return fieldAccess;
    }

    private MethodAccess getMethodAccess( Method method )
    {
        Class<?> definedClass = method.getDeclaringClass();

        MethodAccess methodAccess = methodAccessCache.get( definedClass );
        if ( methodAccess != null )
        {
            return methodAccess;
        }

        methodAccess = MethodAccess.get( definedClass );
        methodAccessCache.put( definedClass, methodAccess );

        return methodAccess;
    }

    private PropertyAccessor buildForField( Field field, Class<?> definedClass )
    {
        final FieldAccess fieldAccess = getFieldAccess( field );
        final int fieldIndex = fieldAccess.getIndex( field.getName() );
        return new FieldValuePropertyAccessor( field, definedClass )
        {

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                fieldAccess.set( instance, fieldIndex, value );
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                return (T) fieldAccess.get( instance, fieldIndex );
            }
        };
    }

    private PropertyAccessor buildForMethod( Method method, Class<?> definedClass )
    {
        final MethodAccess methodAccess = getMethodAccess( method );

        Method getter = BeanUtil.findGetterMethod( method );
        Method setter = BeanUtil.findSetterMethod( method );

        final int getterMethodIndex = methodAccess.getIndex( getter.getName(), method.getParameterTypes() );
        final int setterMethodIndex = methodAccess.getIndex( setter.getName(), method.getParameterTypes() );

        return new MethodValuePropertyAccessor( setter, getter, definedClass )
        {

            @Override
            public void writeShort( Object instance, short value )
            {
                writeObject( instance, value );
            }

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                methodAccess.invoke( instance, setterMethodIndex, value );
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                return (T) methodAccess.invoke( instance, getterMethodIndex );
            }

        };
    }
}
