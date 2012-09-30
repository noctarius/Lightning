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
package org.apache.directmemory.lightning.internal.marshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Random;

import org.apache.directmemory.lightning.Lightning;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.base.AbstractSerializerDefinition;
import org.apache.directmemory.lightning.io.SerializerInputStream;
import org.apache.directmemory.lightning.io.SerializerOutputStream;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.junit.Test;

public class ArrayMarshallerTestCase
{

    private static final Random RANDOM = new Random( -System.nanoTime() );

    @Test
    public void testBooleanArrayMarshalling()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().debugCacheDirectory( new File( "target" ) ).serializerDefinitions( new AbstractSerializerDefinition()
                                                                                                      {

                                                                                                          @Override
                                                                                                          protected void configure()
                                                                                                          {
                                                                                                              serialize(
                                                                                                                         BooleanArray.class ).attributes();
                                                                                                          }
                                                                                                      } ).build();

        BooleanArray test = new BooleanArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (boolean[]) array )[index] = RANDOM.nextBoolean();
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        BooleanArray result = (BooleanArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testByteArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( ByteArray.class ).attributes();
            }
        } );

        ByteArray test = new ByteArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (byte[]) array )[index] = (byte) ( RANDOM.nextInt( 256 ) - 127 );
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        ByteArray result = (ByteArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testCharArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( CharArray.class ).attributes();
            }
        } );

        CharArray test = new CharArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (char[]) array )[index] = (char) ( RANDOM.nextInt( 256 ) - 127 );
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        CharArray result = (CharArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testShortArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( ShortArray.class ).attributes();
            }
        } );

        ShortArray test = new ShortArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (short[]) array )[index] = (short) ( RANDOM.nextInt( 256 ) - 127 );
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        ShortArray result = (ShortArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testIntArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( IntArray.class ).attributes();
            }
        } );

        IntArray test = new IntArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (int[]) array )[index] = RANDOM.nextInt();
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        IntArray result = (IntArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testLongArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( LongArray.class ).attributes();
            }
        } );

        LongArray test = new LongArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (long[]) array )[index] = RANDOM.nextLong();
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        LongArray result = (LongArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testFloatArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( FloatArray.class ).attributes();
            }
        } );

        FloatArray test = new FloatArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (float[]) array )[index] = RANDOM.nextFloat();
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        FloatArray result = (FloatArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testDoubleArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( DoubleArray.class ).attributes();
            }
        } );

        DoubleArray test = new DoubleArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (double[]) array )[index] = RANDOM.nextDouble();
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        DoubleArray result = (DoubleArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testObjectArrayMarshalling()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( ObjectArray.class ).attributes();
            }
        } );

        ObjectArray test = new ObjectArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (Object[]) array )[index] = "Hello-" + RANDOM.nextInt();
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        ObjectArray result = (ObjectArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    @Test
    public void testDeepObjectArrayMarshalling()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().debugCacheDirectory( new File( "target" ) ).serializerDefinitions( new AbstractSerializerDefinition()
                                                                                                      {

                                                                                                          @Override
                                                                                                          protected void configure()
                                                                                                          {
                                                                                                              serialize(
                                                                                                                         DeepObjectArray.class ).attributes();
                                                                                                          }
                                                                                                      } ).build();

        DeepObjectArray test = new DeepObjectArray();
        fillArray( new Predicate()
        {

            @Override
            public void execute( Object array, int index )
            {
                ( (ObjectArray[]) array )[index] = new ObjectArray();
                fillArray( new Predicate()
                {

                    @Override
                    public void execute( Object array, int index )
                    {
                        ( (Object[]) array )[index] = "Hello-" + RANDOM.nextInt();
                    }
                }, ( (ObjectArray[]) array )[index].array );
            }
        }, test.array );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        out.writeObject( test );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        DeepObjectArray result = (DeepObjectArray) in.readObject();

        assertNotNull( result );
        assertEquals( test, result );
    }

    private static void fillArray( Predicate predicate, Object array )
    {
        for ( int i = 0; i < 10; i++ )
        {
            predicate.execute( array, i );
        }
    }

    private static interface Predicate
    {

        void execute( Object array, int index );
    }

    public static class BooleanArray
    {

        @Attribute
        private boolean[] array = new boolean[10];

        public boolean[] getArray()
        {
            return array;
        }

        public void setArray( boolean[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            BooleanArray other = (BooleanArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "BooleanArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class ByteArray
    {

        @Attribute
        private byte[] array = new byte[10];

        public byte[] getArray()
        {
            return array;
        }

        public void setArray( byte[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ByteArray other = (ByteArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "ByteArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class CharArray
    {

        @Attribute
        private char[] array = new char[10];

        public char[] getArray()
        {
            return array;
        }

        public void setArray( char[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            CharArray other = (CharArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "CharArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class ShortArray
    {

        @Attribute
        private short[] array = new short[10];

        public short[] getArray()
        {
            return array;
        }

        public void setArray( short[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ShortArray other = (ShortArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "ShortArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class IntArray
    {

        @Attribute
        private int[] array = new int[10];

        public int[] getArray()
        {
            return array;
        }

        public void setArray( int[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            IntArray other = (IntArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "IntArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class LongArray
    {

        @Attribute
        private long[] array = new long[10];

        public long[] getArray()
        {
            return array;
        }

        public void setArray( long[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            LongArray other = (LongArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "LongArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class FloatArray
    {

        @Attribute
        private float[] array = new float[10];

        public float[] getArray()
        {
            return array;
        }

        public void setArray( float[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            FloatArray other = (FloatArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "FloatArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class DoubleArray
    {

        @Attribute
        private double[] array = new double[10];

        public double[] getArray()
        {
            return array;
        }

        public void setArray( double[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            DoubleArray other = (DoubleArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "DoubleArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class ObjectArray
    {

        @Attribute
        private String[] array = new String[10];

        public String[] getArray()
        {
            return array;
        }

        public void setArray( String[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ObjectArray other = (ObjectArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "ObjectArray [array=" + Arrays.toString( array ) + "]";
        }
    }

    public static class DeepObjectArray
    {

        @Attribute
        private ObjectArray[] array = new ObjectArray[10];

        public ObjectArray[] getArray()
        {
            return array;
        }

        public void setArray( ObjectArray[] array )
        {
            this.array = array;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( array );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            DeepObjectArray other = (DeepObjectArray) obj;
            if ( !Arrays.equals( array, other.array ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "DeepObjectArray [array=" + Arrays.toString( array ) + "]";
        }
    }
}
