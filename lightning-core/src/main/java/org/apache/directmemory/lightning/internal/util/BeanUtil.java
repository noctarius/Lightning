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
package org.apache.directmemory.lightning.internal.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.directmemory.lightning.exceptions.SerializerDefinitionException;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;
import org.objectweb.asm.Type;

public final class BeanUtil
{

    private BeanUtil()
    {
    }

    public static Set<Field> findPropertiesByClass( Class<?> type, Class<? extends Annotation> attributeAnnotation )
    {
        Set<Field> properties = new HashSet<Field>();
        properties.addAll( findPropertiesByInstanceFields( type, attributeAnnotation ) );
        properties.addAll( findPropertiesByMethods( type, type, attributeAnnotation ) );
        properties.addAll( findPropertiesByInterfaces( type, attributeAnnotation ) );

        if ( type.getSuperclass() != null && type.getSuperclass() != Object.class )
        {
            properties.addAll( findPropertiesByClass( type.getSuperclass(), attributeAnnotation ) );
        }

        return properties;
    }

    public static Set<Field> findPropertiesByInstanceFields( Class<?> type,
                                                             Class<? extends Annotation> attributeAnnotation )
    {
        Set<Field> attributes = new HashSet<Field>();
        for ( Field field : type.getDeclaredFields() )
        {
            if ( field.isAnnotationPresent( attributeAnnotation ) )
            {
                attributes.add( field );
            }
        }

        return attributes;
    }

    public static Set<Field> findPropertiesByMethods( Class<?> type, Class<?> searchType,
                                                      Class<? extends Annotation> attributeAnnotation )
    {
        Set<Field> attributes = new HashSet<Field>();
        for ( Method method : searchType.getDeclaredMethods() )
        {
            if ( method.isAnnotationPresent( attributeAnnotation ) )
            {
                String propertyName = BeanUtil.buildPropertyName( method );
                Field field = BeanUtil.getFieldByPropertyName( propertyName, type );
                if ( field == null )
                {
                    if ( attributeAnnotation == Attribute.class )
                    {
                        Attribute attribute = method.getAnnotation( Attribute.class );
                        field = BeanUtil.getFieldByPropertyName( attribute.property(), type );
                    }

                    if ( field == null )
                    {
                        throw new SerializerDefinitionException( "No property for method " + method + " was found" );
                    }
                }

                attributes.add( field );
            }
        }

        return attributes;
    }

    public static Set<Field> findPropertiesByInterfaces( Class<?> type, Class<? extends Annotation> attributeAnnotation )
    {
        Set<Field> attributes = new HashSet<Field>();

        for ( Class<?> interfaze : type.getInterfaces() )
        {
            // Add all annotated methods in interface
            attributes.addAll( findInterfaceProperties0( type, interfaze, attributeAnnotation ) );
        }

        return attributes;
    }

    private static Set<Field> findInterfaceProperties0( Class<?> type, Class<?> interfaze,
                                                        Class<? extends Annotation> attributeAnnotation )
    {
        Set<Field> attributes = new HashSet<Field>();

        // Add all annotated methods in interface
        attributes.addAll( findPropertiesByMethods( type, interfaze, attributeAnnotation ) );

        // Look up super-interface
        if ( interfaze.getSuperclass() != null )
        {
            attributes.addAll( findInterfaceProperties0( type, interfaze.getSuperclass(), attributeAnnotation ) );
        }

        return attributes;
    }

    public static Field getFieldByPropertyName( String propertyName, Class<?> type )
    {
        try
        {
            return type.getDeclaredField( propertyName );
        }
        catch ( NoSuchFieldException e )
        {
            if ( type.getSuperclass() != null && type.getSuperclass() != Object.class )
            {
                return getFieldByPropertyName( propertyName, type.getSuperclass() );
            }
            return null;
        }
    }

    public static Method findSetterMethod( Method method )
    {
        if ( method.getName().startsWith( "set" ) )
        {
            return method;
        }

        String propertyName = StringUtil.toUpperCamelCase( extractPropertyName( method.getName() ) );

        Class<?> type = method.getReturnType();
        Class<?> clazz = method.getDeclaringClass();
        String setterName = "set" + propertyName;

        try
        {
            return clazz.getDeclaredMethod( setterName, type );
        }
        catch ( Exception e )
        {
            // Seems there's no setter, so ignore all exceptions
            return null;
        }
    }

    public static Method findArraySetterMethod( Method method )
    {
        if ( method.getName().startsWith( "set" ) )
        {
            return method;
        }

        String propertyName = StringUtil.toUpperCamelCase( extractPropertyName( method.getName() ) );

        Class<?> type = method.getReturnType();
        Class<?> clazz = method.getDeclaringClass();
        String setterName = "set" + propertyName;

        try
        {
            return clazz.getDeclaredMethod( setterName, type, int.class );
        }
        catch ( Exception e )
        {
            // Seems there's no setter, so ignore all exceptions
            return null;
        }
    }

    public static Method findGetterMethod( Method method )
    {
        if ( method.getName().startsWith( "get" ) || method.getName().startsWith( "is" ) )
        {
            return method;
        }

        String propertyName = StringUtil.toUpperCamelCase( extractPropertyName( method.getName() ) );

        Class<?> type = method.getParameterTypes()[0];
        Class<?> clazz = method.getDeclaringClass();
        String getterObjectName = "get" + propertyName;
        String getterBooleanName = "is" + propertyName;

        try
        {
            return clazz.getDeclaredMethod( getterObjectName, type );
        }
        catch ( Exception e )
        {
            if ( type == boolean.class )
            {
                try
                {
                    return clazz.getDeclaredMethod( getterBooleanName, type );
                }
                catch ( Exception ex )
                {
                    // Intentionally left blank - just fall through
                }
            }

            // Seems there's no setter, so ignore all exceptions
            return null;
        }
    }

    public static Method findArrayGetterMethod( Method method )
    {
        if ( method.getName().startsWith( "get" ) || method.getName().startsWith( "is" ) )
        {
            return method;
        }

        String propertyName = StringUtil.toUpperCamelCase( extractPropertyName( method.getName() ) );

        Class<?> type = method.getParameterTypes()[0];
        Class<?> clazz = method.getDeclaringClass();
        String getterObjectName = "get" + propertyName;
        String getterBooleanName = "is" + propertyName;

        try
        {
            return clazz.getDeclaredMethod( getterObjectName, type );
        }
        catch ( Exception e )
        {
            if ( type == boolean.class )
            {
                try
                {
                    return clazz.getDeclaredMethod( getterBooleanName, type, int.class );
                }
                catch ( Exception ex )
                {
                    // Intentionally left blank - just fall through
                }
            }

            // Seems there's no setter, so ignore all exceptions
            return null;
        }
    }

    public static String buildPropertyName( Method method )
    {
        return buildPropertyName( method.getName() );
    }

    public static String buildPropertyName( String methodName )
    {
        return StringUtil.toLowerCamelCase( extractPropertyName( methodName ) );
    }

    public static String buildInternalSignature( Iterable<PropertyDescriptor> propertyDescriptors )
    {
        StringBuilder internalSignature = new StringBuilder();
        for ( PropertyDescriptor propertyDescriptor : propertyDescriptors )
        {
            internalSignature.append( propertyDescriptor.getInternalSignature() );
        }
        return internalSignature.toString();
    }

    public static <T> String buildInternalSignature( String propertyName, PropertyAccessor propertyAccessor )
    {
        String type = Type.getDescriptor( propertyAccessor.getType() );
        return new StringBuilder( "{" ).append( propertyName ).append( "}" ).append( type ).toString();
    }

    private static String extractPropertyName( String methodName )
    {
        if ( methodName.toUpperCase().startsWith( "GET" ) || methodName.toUpperCase().startsWith( "IS" )
            || methodName.toUpperCase().startsWith( "SET" ) )
        {

            char[] characters = methodName.toCharArray();
            for ( int i = 1; i < characters.length; i++ )
            {
                if ( Character.isUpperCase( characters[i] ) )
                {
                    return StringUtil.toLowerCamelCase( methodName.substring( i ) );
                }
            }
        }
        return StringUtil.toLowerCamelCase( methodName );
    }
}
