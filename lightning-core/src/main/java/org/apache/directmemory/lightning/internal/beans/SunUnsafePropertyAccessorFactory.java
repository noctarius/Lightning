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

import org.apache.directmemory.lightning.internal.util.UnsafeUtil;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;

@SuppressWarnings( "restriction" )
final class SunUnsafePropertyAccessorFactory
    implements PropertyAccessorFactory
{

    private static final sun.misc.Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    SunUnsafePropertyAccessorFactory()
    {
    }

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
        throw new UnsupportedOperationException( "Method access is not supported by Unsafe style" );
    }

    private PropertyAccessor buildForValueField( final Field field, final Class<?> definedClass )
    {
        return new FieldValuePropertyAccessor( field, definedClass )
        {

            private final long offset;

            {
                offset = UNSAFE.objectFieldOffset( field );
            }

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                UNSAFE.putObject( instance, offset, value );
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                return (T) UNSAFE.getObject( instance, offset );
            }

            @Override
            public void writeBoolean( Object instance, boolean value )
            {
                UNSAFE.putBoolean( instance, offset, value );
            }

            @Override
            public boolean readBoolean( Object instance )
            {
                return UNSAFE.getBoolean( instance, offset );
            }

            @Override
            public void writeByte( Object instance, byte value )
            {
                UNSAFE.putByte( instance, offset, value );
            }

            @Override
            public byte readByte( Object instance )
            {
                return UNSAFE.getByte( instance, offset );
            }

            @Override
            public void writeShort( Object instance, short value )
            {
                UNSAFE.putShort( instance, offset, value );
            }

            @Override
            public short readShort( Object instance )
            {
                return UNSAFE.getShort( instance, offset );
            }

            @Override
            public void writeChar( Object instance, char value )
            {
                UNSAFE.putChar( instance, offset, value );
            }

            @Override
            public char readChar( Object instance )
            {
                return UNSAFE.getChar( instance, offset );
            }

            @Override
            public void writeInt( Object instance, int value )
            {
                UNSAFE.putInt( instance, offset, value );
            }

            @Override
            public int readInt( Object instance )
            {
                return UNSAFE.getInt( instance, offset );
            }

            @Override
            public void writeLong( Object instance, long value )
            {
                UNSAFE.putLong( instance, offset, value );
            }

            @Override
            public long readLong( Object instance )
            {
                return UNSAFE.getLong( instance, offset );
            }

            @Override
            public void writeFloat( Object instance, float value )
            {
                UNSAFE.putFloat( instance, offset, value );
            }

            @Override
            public float readFloat( Object instance )
            {
                return UNSAFE.getFloat( instance, offset );
            }

            @Override
            public void writeDouble( Object instance, double value )
            {
                UNSAFE.putDouble( instance, offset, value );
            }

            @Override
            public double readDouble( Object instance )
            {
                return UNSAFE.getDouble( instance, offset );
            }
        };
    }

    private PropertyAccessor buildForArrayField( final Field field, final Class<?> definedClass )
    {
        return new FieldArrayPropertyAccessor( field, definedClass )
        {

            private final long offset;

            private final int arrayBaseOffset;

            private final int arrayIndexScale;

            {
                offset = UNSAFE.objectFieldOffset( field );
                arrayBaseOffset = UNSAFE.arrayBaseOffset( field.getType() );
                arrayIndexScale = UNSAFE.arrayIndexScale( field.getType() );
            }

            @Override
            public <T> void writeObject( Object instance, T value )
            {
                UNSAFE.putObject( instance, offset, value );
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance )
            {
                return (T) UNSAFE.getObject( instance, offset );
            }

            @Override
            public <T> void writeObject( Object instance, int index, T value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putObject( instance, offset, value );
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public <T> T readObject( Object instance, int index )
            {
                long localOffset = calculateIndexOffset( index );
                return (T) UNSAFE.getObject( instance, localOffset );
            }

            @Override
            public void writeBoolean( Object instance, int index, boolean value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putBoolean( instance, offset, value );
            }

            @Override
            public boolean readBoolean( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getBoolean( instance, offset );
            }

            @Override
            public void writeByte( Object instance, int index, byte value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putByte( instance, offset, value );
            }

            @Override
            public byte readByte( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getByte( instance, offset );
            }

            @Override
            public void writeShort( Object instance, int index, short value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putShort( instance, offset, value );
            }

            @Override
            public short readShort( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getShort( instance, offset );
            }

            @Override
            public void writeChar( Object instance, int index, char value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putChar( instance, offset, value );
            }

            @Override
            public char readChar( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getChar( instance, offset );
            }

            @Override
            public void writeInt( Object instance, int index, int value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putInt( instance, offset, value );
            }

            @Override
            public int readInt( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getInt( instance, offset );
            }

            @Override
            public void writeLong( Object instance, int index, long value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putLong( instance, offset, value );
            }

            @Override
            public long readLong( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getLong( instance, offset );
            }

            @Override
            public void writeFloat( Object instance, int index, float value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putFloat( instance, offset, value );
            }

            @Override
            public float readFloat( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getFloat( instance, offset );
            }

            @Override
            public void writeDouble( Object instance, int index, double value )
            {
                long offset = calculateIndexOffset( index );
                UNSAFE.putDouble( instance, offset, value );
            }

            @Override
            public double readDouble( Object instance, int index )
            {
                long offset = calculateIndexOffset( index );
                return UNSAFE.getDouble( instance, offset );
            }

            private long calculateIndexOffset( int index )
            {
                return arrayBaseOffset + ( index * arrayIndexScale );
            }
        };
    }
}
