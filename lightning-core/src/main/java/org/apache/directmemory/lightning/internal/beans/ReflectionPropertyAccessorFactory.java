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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.directmemory.lightning.exceptions.IllegalPropertyAccessException;
import org.apache.directmemory.lightning.internal.util.BeanUtil;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;

public class ReflectionPropertyAccessorFactory
    implements PropertyAccessorFactory
{

    @Override
    public PropertyAccessor fieldAccess( Field field, Class<?> definedClass )
    {
        if ( field.getType().isArray() )
        {
            return buildForArrayField( field, definedClass );
        }

        return buildForValueField( field, definedClass );
    }

    @Override
    public PropertyAccessor methodAccess( Method method, Class<?> definedClass )
    {
        if ( method.getReturnType().isArray() )
        {
            return buildForArrayMethod( method, definedClass );
        }

        return buildForValueMethod( method, definedClass );
    }

    private PropertyAccessor buildForValueField( final Field field, final Class<?> definedClass )
    {
        field.setAccessible( true );
        return new FieldValuePropertyAccessor( field, definedClass )
        {

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                try
                {
                    getField().set( instance, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while writing field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                try
                {
                    return (T) getField().get( instance );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }
        };
    }

    private PropertyAccessor buildForArrayField( final Field field, final Class<?> definedClass )
    {
        field.setAccessible( true );
        return new FieldArrayPropertyAccessor( field, definedClass )
        {

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                try
                {
                    getField().set( instance, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while writing field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                try
                {
                    return (T) getField().get( instance );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public <T> void writeObject( Object instance, int index, T value )
            {
                try
                {
                    Array.set( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while writing field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance, int index )
            {
                try
                {
                    return (T) Array.get( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeShort( Object instance, int index, short value )
            {
                try
                {
                    Array.setShort( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeLong( Object instance, int index, long value )
            {
                try
                {
                    Array.setLong( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeInt( Object instance, int index, int value )
            {
                try
                {
                    Array.setInt( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeFloat( Object instance, int index, float value )
            {
                try
                {
                    Array.setFloat( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeDouble( Object instance, int index, double value )
            {
                try
                {
                    Array.setDouble( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeChar( Object instance, int index, char value )
            {
                try
                {
                    Array.setChar( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeByte( Object instance, int index, byte value )
            {
                try
                {
                    Array.setByte( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public void writeBoolean( Object instance, int index, boolean value )
            {
                try
                {
                    Array.setBoolean( instance, index, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public short readShort( Object instance, int index )
            {
                try
                {
                    return Array.getShort( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public long readLong( Object instance, int index )
            {
                try
                {
                    return Array.getLong( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public int readInt( Object instance, int index )
            {
                try
                {
                    return Array.getInt( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public float readFloat( Object instance, int index )
            {
                try
                {
                    return Array.getFloat( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public double readDouble( Object instance, int index )
            {
                try
                {
                    return Array.getDouble( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public char readChar( Object instance, int index )
            {
                try
                {
                    return Array.getChar( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public byte readByte( Object instance, int index )
            {
                try
                {
                    return Array.getByte( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }

            @Override
            public boolean readBoolean( Object instance, int index )
            {
                try
                {
                    return Array.getBoolean( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading field " + getField().getName(),
                                                              e );
                }
            }
        };
    }

    private PropertyAccessor buildForValueMethod( Method method, Class<?> definedClass )
    {
        Method getter = BeanUtil.findGetterMethod( method );
        Method setter = BeanUtil.findSetterMethod( method );

        getter.setAccessible( true );
        setter.setAccessible( true );

        return new MethodValuePropertyAccessor( setter, getter, definedClass )
        {

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                try
                {
                    getSetterMethod().invoke( instance, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while writing with method "
                        + getSetterMethod().getName(), e );
                }
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                try
                {
                    return (T) getGetterMethod().invoke( instance );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading with method "
                        + getGetterMethod().getName(), e );
                }
            }
        };
    }

    private PropertyAccessor buildForArrayMethod( Method method, Class<?> definedClass )
    {
        final Method getter = BeanUtil.findGetterMethod( method );
        final Method setter = BeanUtil.findSetterMethod( method );
        final Method arrayGetter = BeanUtil.findArrayGetterMethod( method );
        final Method arraySetter = BeanUtil.findArraySetterMethod( method );

        getter.setAccessible( true );
        setter.setAccessible( true );

        return new MethodArrayPropertyAccessor( setter, getter, definedClass )
        {

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                try
                {
                    getSetterMethod().invoke( instance, value );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while writing with method "
                        + getSetterMethod().getName(), e );
                }
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                try
                {
                    return (T) getGetterMethod().invoke( instance );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading with method "
                        + getGetterMethod().getName(), e );
                }
            }

            @Override
            public <T> void writeObject( Object instance, int index, T value )
            {
                try
                {
                    arraySetter.invoke( instance, value, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while writing with method "
                        + getSetterMethod().getName(), e );
                }
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance, int index )
            {
                try
                {
                    return (T) arrayGetter.invoke( instance, index );
                }
                catch ( Exception e )
                {
                    throw new IllegalPropertyAccessException( "Exception while reading with method "
                        + getGetterMethod().getName(), e );
                }
            }
        };
    }
}
