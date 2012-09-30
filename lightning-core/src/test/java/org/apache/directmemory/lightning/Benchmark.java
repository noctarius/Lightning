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
package org.apache.directmemory.lightning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.directmemory.lightning.Lightning;
import org.apache.directmemory.lightning.SerializationContext;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.base.AbstractObjectMarshaller;
import org.apache.directmemory.lightning.base.AbstractSerializerDefinition;
import org.apache.directmemory.lightning.io.SerializerInputStream;
import org.apache.directmemory.lightning.io.SerializerOutputStream;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class Benchmark
{

    private static final int WARMUP_ROUNDS = 100000;

    private static final int BENCHMARK_ROUNDS = 800000;

    @Test
    public void benchmarkLightningSerialization()
        throws Exception
    {
        long buildStartTime = System.nanoTime();
        Serializer serializer =
            Lightning.newBuilder().debugCacheDirectory( new File( "target" ) ).serializerDefinitions( new BenchmarkSerializerDefinition() ).build();
        long nanos = TimeUnit.NANOSECONDS.toMillis( System.nanoTime() - buildStartTime );
        System.out.println( "Lightning Serializer build time: " + nanos + " ms" );

        long size = 0;
        for ( int i = 0; i < WARMUP_ROUNDS; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SerializerOutputStream out = new SerializerOutputStream( baos, serializer );
            Foo foo = buildRandomFoo();
            out.writeObject( foo );

            assertNotNull( baos );
            assertNotNull( out );
            assertNotNull( baos.toByteArray() );
            size = baos.toByteArray().length;
        }

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }

        long time = 0;
        for ( int i = 0; i < BENCHMARK_ROUNDS; i++ )
        {
            Foo foo = buildRandomFoo();

            long startTime = System.nanoTime();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SerializerOutputStream out = new SerializerOutputStream( baos, serializer );
            out.writeObject( foo );

            time += System.nanoTime() - startTime;
            assertNotNull( baos.toByteArray() );
        }

        double avg = time / (double) BENCHMARK_ROUNDS;
        System.out.println( "Lightning Serialization Avg: " + String.format( "%5.2f", avg ) + " ns, runs: "
            + BENCHMARK_ROUNDS + ", size: " + size + " bytes" );

        System.runFinalization();
        System.gc();

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }
    }

    @Test
    public void benchmarkLightningDeserialization()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().serializerDefinitions( new BenchmarkSerializerDefinition() ).build();

        long size = 0;
        for ( int i = 0; i < WARMUP_ROUNDS; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SerializerOutputStream out = new SerializerOutputStream( baos, serializer );
            Foo foo = buildRandomFoo();
            out.writeObject( foo );

            assertNotNull( baos );
            assertNotNull( out );
            assertNotNull( baos.toByteArray() );
            size = baos.toByteArray().length;

            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
            SerializerInputStream in = new SerializerInputStream( bais, serializer );
            Object value = in.readObject();
            assertNotNull( value );
            assertEquals( foo, value );
        }

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }

        long time = 0;
        for ( int i = 0; i < BENCHMARK_ROUNDS; i++ )
        {
            Foo foo = buildRandomFoo();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SerializerOutputStream out = new SerializerOutputStream( baos, serializer );
            out.writeObject( foo );

            long startTime = System.nanoTime();
            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
            SerializerInputStream in = new SerializerInputStream( bais, serializer );
            Object value = in.readObject();
            time += System.nanoTime() - startTime;
            assertNotNull( value );
            assertEquals( foo, value );
        }

        double avg = time / (double) BENCHMARK_ROUNDS;
        System.out.println( "Lightning Deserialization Avg: " + String.format( "%5.2f", avg ) + " ns, runs: "
            + BENCHMARK_ROUNDS + ", size: " + size + " bytes" );

        System.runFinalization();
        System.gc();

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }
    }

    @Test
    public void benchmarkJavaSerialization()
        throws Exception
    {
        long size = 0;
        for ( int i = 0; i < WARMUP_ROUNDS; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );
            Foo foo = buildRandomFoo();
            out.writeObject( foo );

            assertNotNull( baos );
            assertNotNull( out );
            assertNotNull( baos.toByteArray() );
            size = baos.toByteArray().length;
        }

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }

        long time = 0;
        for ( int i = 0; i < BENCHMARK_ROUNDS; i++ )
        {
            Foo foo = buildRandomFoo();

            long startTime = System.nanoTime();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );
            out.writeObject( foo );

            time += System.nanoTime() - startTime;
            assertNotNull( baos.toByteArray() );
        }

        double avg = time / (double) BENCHMARK_ROUNDS;
        System.out.println( "Java Serialization Avg: " + String.format( "%5.2f", avg ) + " ns, runs: "
            + BENCHMARK_ROUNDS + ", size: " + size + " bytes" );

        System.runFinalization();
        System.gc();

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }
    }

    @Test
    public void benchmarkJavaDeserialization()
        throws Exception
    {
        long size = 0;
        for ( int i = 0; i < WARMUP_ROUNDS; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );
            Foo foo = buildRandomFoo();
            out.writeObject( foo );

            assertNotNull( baos );
            assertNotNull( out );
            assertNotNull( baos.toByteArray() );
            size = baos.toByteArray().length;

            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
            ObjectInputStream in = new ObjectInputStream( bais );
            Object value = in.readObject();
            assertNotNull( value );
            assertEquals( foo, value );
        }

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }

        long time = 0;
        for ( int i = 0; i < BENCHMARK_ROUNDS; i++ )
        {
            Foo foo = buildRandomFoo();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );
            out.writeObject( foo );

            long startTime = System.nanoTime();
            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
            ObjectInputStream in = new ObjectInputStream( bais );
            Object value = in.readObject();

            time += System.nanoTime() - startTime;
            assertNotNull( value );
            assertEquals( foo, value );
        }

        double avg = time / (double) BENCHMARK_ROUNDS;
        System.out.println( "Java Deserialization Avg: " + String.format( "%5.2f", avg ) + " ns, runs: "
            + BENCHMARK_ROUNDS + ", size: " + size + " bytes" );

        System.runFinalization();
        System.gc();

        try
        {
            Thread.sleep( 5000 );
        }
        catch ( Exception e )
        {
        }
    }

    private static final Random RANDOM = new Random( System.nanoTime() );

    private static final String[] STRING_VALUES = { "HGHO", "jldu", "oösd", "JKGH", "HGFG", "JLHL", "GJJK", "JKGH" };

    private static Foo buildRandomFoo()
    {
        Foo foo = new Foo();
        foo.enumValue = RANDOM.nextInt( 100 ) < 50 ? Bar.Value1 : Bar.Value2;
        foo.someOther = RANDOM.nextInt();
        foo.value = RANDOM.nextInt( 100 ) < 50 ? null : RANDOM.nextInt();
        foo.first = STRING_VALUES[RANDOM.nextInt( STRING_VALUES.length )];
        foo.second = STRING_VALUES[RANDOM.nextInt( STRING_VALUES.length )];

        return foo;
    }

    public static class BenchmarkSerializerDefinition
        extends AbstractSerializerDefinition
    {

        @Override
        protected void configure()
        {
            serialize( Foo.class ).attributes();
        }
    }

    @SuppressWarnings( "serial" )
    public static class Foo
        implements Serializable
    {

        private String first;

        private String second;

        private Integer value;

        private int someOther;

        @Attribute
        private Bar enumValue;

        @Attribute
        public String getFirst()
        {
            return first;
        }

        public void setFirst( String first )
        {
            this.first = first;
        }

        @Attribute
        public String getSecond()
        {
            return second;
        }

        public void setSecond( String second )
        {
            this.second = second;
        }

        @Attribute
        public Integer getValue()
        {
            return value;
        }

        public void setValue( Integer value )
        {
            this.value = value;
        }

        @Attribute
        // Implicitly required
        public int getSomeOther()
        {
            return someOther;
        }

        public void setSomeOther( int someOther )
        {
            this.someOther = someOther;
        }

        public Bar getEnumValue()
        {
            return enumValue;
        }

        public void setEnumValue( Bar enumValue )
        {
            this.enumValue = enumValue;
        }

        @Override
        public String toString()
        {
            return "Foo [hash=@" + hashCode() + ", first=" + first + ", second=" + second + ", value=" + value
                + ", someOther=" + someOther + ", enumValue=" + enumValue + "]";
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( enumValue == null ) ? 0 : enumValue.hashCode() );
            result = prime * result + ( ( first == null ) ? 0 : first.hashCode() );
            result = prime * result + ( ( second == null ) ? 0 : second.hashCode() );
            result = prime * result + someOther;
            result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
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
            Foo other = (Foo) obj;
            if ( enumValue != other.enumValue )
                return false;
            if ( first == null )
            {
                if ( other.first != null )
                    return false;
            }
            else if ( !first.equals( other.first ) )
                return false;
            if ( second == null )
            {
                if ( other.second != null )
                    return false;
            }
            else if ( !second.equals( other.second ) )
                return false;
            if ( someOther != other.someOther )
                return false;
            if ( value == null )
            {
                if ( other.value != null )
                    return false;
            }
            else if ( !value.equals( other.value ) )
                return false;
            return true;
        }
    }

    public static enum Bar
    {
        Value1, Value2
    }

    public static class BarMarshaller
        extends AbstractObjectMarshaller
    {

        @Override
        public boolean acceptType( Class<?> type )
        {
            return type == Bar.class;
        }

        @Override
        public void marshall( Object value, PropertyDescriptor propertyDescriptor, DataOutput dataOutput,
                              SerializationContext serializationContext )
            throws IOException
        {
        }

        @Override
        public <V> V unmarshall( V value, PropertyDescriptor propertyDescriptor, DataInput dataInput,
                                 SerializationContext serializationContext )
            throws IOException
        {

            return null;
        }
    }

    public static class SomeSpecialIntegerMarshaller
        extends AbstractObjectMarshaller
    {

        @Override
        public boolean acceptType( Class<?> type )
        {
            return type == Integer.class;
        }

        @Override
        public void marshall( Object value, PropertyDescriptor propertyDescriptor, DataOutput dataOutput,
                              SerializationContext serializationContext )
            throws IOException
        {
        }

        @Override
        public <V> V unmarshall( V value, PropertyDescriptor propertyDescriptor, DataInput dataInput,
                                 SerializationContext serializationContext )
            throws IOException
        {

            return value;
        }

    }
}
