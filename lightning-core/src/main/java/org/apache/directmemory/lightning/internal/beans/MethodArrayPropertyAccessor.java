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

import java.lang.reflect.Method;

import org.apache.directmemory.lightning.metadata.AccessorType;
import org.apache.directmemory.lightning.metadata.ArrayPropertyAccessor;

public abstract class MethodArrayPropertyAccessor
    extends AbstractValuePropertyAccessor
    implements ArrayPropertyAccessor
{

    private final Method setter;

    private final Method getter;

    private final Class<?> definedClass;

    protected MethodArrayPropertyAccessor( Method setter, Method getter, Class<?> definedClass )
    {
        this.setter = setter;
        this.getter = getter;
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
    public Class<?> getDeclaringClass()
    {
        return getter.getDeclaringClass();
    }

    @Override
    public AccessorType getAccessorType()
    {
        return AccessorType.Method;
    }

    @Override
    public Class<?> getType()
    {
        return getter.getReturnType();
    }

    protected Method getGetterMethod()
    {
        return getter;
    }

    protected Method getSetterMethod()
    {
        return setter;
    }

    @Override
    public void writeBoolean( Object instance, int index, boolean value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public boolean readBoolean( Object instance, int index )
    {
        return (Boolean) readObject( instance, index );
    }

    @Override
    public void writeByte( Object instance, int index, byte value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public byte readByte( Object instance, int index )
    {
        return (Byte) readObject( instance, index );
    }

    @Override
    public void writeChar( Object instance, int index, char value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public char readChar( Object instance, int index )
    {
        return (Character) readObject( instance, index );
    }

    @Override
    public void writeShort( Object instance, int index, short value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public short readShort( Object instance, int index )
    {
        return (Short) readObject( instance, index );
    }

    @Override
    public void writeInt( Object instance, int index, int value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public int readInt( Object instance, int index )
    {
        return (Integer) readObject( instance, index );
    }

    @Override
    public void writeLong( Object instance, int index, long value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public long readLong( Object instance, int index )
    {
        return (Long) readObject( instance, index );
    }

    @Override
    public void writeFloat( Object instance, int index, float value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public float readFloat( Object instance, int index )
    {
        return (Float) readObject( instance, index );
    }

    @Override
    public void writeDouble( Object instance, int index, double value )
    {
        writeObject( instance, index, value );
    }

    @Override
    public double readDouble( Object instance, int index )
    {
        return (Double) readObject( instance, index );
    }
}
