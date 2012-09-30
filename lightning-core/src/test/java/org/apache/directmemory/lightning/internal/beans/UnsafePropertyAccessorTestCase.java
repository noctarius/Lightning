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
import java.util.Arrays;

import org.apache.directmemory.lightning.internal.beans.SunUnsafePropertyAccessorFactory;
import org.apache.directmemory.lightning.metadata.ArrayPropertyAccessor;
import org.junit.Test;

public class UnsafePropertyAccessorTestCase
{

    @Test
    public void testUnsafeBooleanArray()
        throws Exception
    {
        class BooleanArrayTest
        {

            private boolean[] array = new boolean[10];
        }

        Field field = BooleanArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, BooleanArrayTest.class );

        BooleanArrayTest test = new BooleanArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = i % 2 == 0 ? true : false;
        }

        BooleanArrayTest result = new BooleanArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            boolean value = propertyAccessor.readBoolean( test.array, i );
            propertyAccessor.writeBoolean( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }

    @Test
    public void testUnsafeByteArray()
        throws Exception
    {
        class ByteArrayTest
        {

            private byte[] array = new byte[10];
        }

        Field field = ByteArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, ByteArrayTest.class );

        ByteArrayTest test = new ByteArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = (byte) ( i + 1 );
        }

        ByteArrayTest result = new ByteArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            byte value = propertyAccessor.readByte( test.array, i );
            propertyAccessor.writeByte( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }

    @Test
    public void testUnsafeShortArray()
        throws Exception
    {
        class ShortArrayTest
        {

            private short[] array = new short[10];
        }

        Field field = ShortArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, ShortArrayTest.class );

        ShortArrayTest test = new ShortArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = (short) ( i + 1 );
        }

        ShortArrayTest result = new ShortArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            short value = propertyAccessor.readShort( test.array, i );
            propertyAccessor.writeShort( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }

    @Test
    public void testUnsafeIntArray()
        throws Exception
    {
        class IntArrayTest
        {

            private int[] array = new int[10];
        }

        Field field = IntArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, IntArrayTest.class );

        IntArrayTest test = new IntArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = i + 1;
        }

        IntArrayTest result = new IntArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            int value = propertyAccessor.readInt( test.array, i );
            propertyAccessor.writeInt( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }

    @Test
    public void testUnsafeLongArray()
        throws Exception
    {
        class LongArrayTest
        {

            private long[] array = new long[10];
        }

        Field field = LongArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, LongArrayTest.class );

        LongArrayTest test = new LongArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = i + 1;
        }

        LongArrayTest result = new LongArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            long value = propertyAccessor.readLong( test.array, i );
            propertyAccessor.writeLong( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }

    @Test
    public void testUnsafeFloatArray()
        throws Exception
    {
        class FloatArrayTest
        {

            private float[] array = new float[10];
        }

        Field field = FloatArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, FloatArrayTest.class );

        FloatArrayTest test = new FloatArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = (float) ( i + 1. );
        }

        FloatArrayTest result = new FloatArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            float value = propertyAccessor.readFloat( test.array, i );
            propertyAccessor.writeFloat( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }

    @Test
    public void testUnsafeDoubleArray()
        throws Exception
    {
        class DoubleArrayTest
        {

            private double[] array = new double[10];
        }

        Field field = DoubleArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, DoubleArrayTest.class );

        DoubleArrayTest test = new DoubleArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = i + 1.;
        }

        DoubleArrayTest result = new DoubleArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            double value = propertyAccessor.readDouble( test.array, i );
            propertyAccessor.writeDouble( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }

    @Test
    public void testUnsafeObjectArray()
        throws Exception
    {
        class ObjectArrayTest
        {

            private String[] array = new String[10];
        }

        Field field = ObjectArrayTest.class.getDeclaredField( "array" );
        ArrayPropertyAccessor propertyAccessor =
            (ArrayPropertyAccessor) new SunUnsafePropertyAccessorFactory().fieldAccess( field, ObjectArrayTest.class );

        ObjectArrayTest test = new ObjectArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            test.array[i] = "Hello" + i;
        }

        ObjectArrayTest result = new ObjectArrayTest();
        for ( int i = 0; i < 10; i++ )
        {
            Object value = propertyAccessor.readObject( test.array, i );
            propertyAccessor.writeObject( result.array, i, value );
        }

        Arrays.equals( test.array, result.array );
    }
}
