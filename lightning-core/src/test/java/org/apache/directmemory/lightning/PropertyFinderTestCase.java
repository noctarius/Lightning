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
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.directmemory.lightning.Lightning;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.base.AbstractSerializerDefinition;
import org.apache.directmemory.lightning.internal.ClassDescriptorAwareSerializer;
import org.apache.directmemory.lightning.io.SerializerInputStream;
import org.apache.directmemory.lightning.io.SerializerOutputStream;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.apache.directmemory.lightning.metadata.ClassDescriptor;
import org.junit.Test;

public class PropertyFinderTestCase
{

    @Test
    public void testCustomDefinedPropertyFind1()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( Standard.class ).attributes( attribute( "value1" ) );
                serialize( Standard.class ).attributes( attribute( "value2" ) );
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( Standard.class );

        assertNotNull( classDescriptor );
        assertEquals( 2, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        Standard standard = new Standard();
        standard.setValue1( "Foo" );
        standard.setValue2( 321 );
        out.writeObject( standard );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        Standard result = (Standard) in.readObject();

        assertEquals( standard, result );
    }

    @Test
    public void testCustomDefinedPropertyFind2()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( Standard.class ).attributes( attribute( "value1" ), attribute( "value2" ) );
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( Standard.class );

        assertNotNull( classDescriptor );
        assertEquals( 2, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        Standard standard = new Standard();
        standard.setValue1( "Foo" );
        standard.setValue2( 321 );
        out.writeObject( standard );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        Standard result = (Standard) in.readObject();

        assertEquals( standard, result );
    }

    @Test
    public void testStandardPropertyFind()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( Standard.class ).attributes();
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( Standard.class );

        assertNotNull( classDescriptor );
        assertEquals( 2, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        Standard standard = new Standard();
        standard.setValue1( "Foo" );
        standard.setValue2( 321 );
        out.writeObject( standard );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        Standard result = (Standard) in.readObject();

        assertEquals( standard, result );
    }

    @Test
    public void testStandardPropertyFindUsingExclude()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( Standard.class ).attributes().exclude( "value1" );
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( Standard.class );

        assertNotNull( classDescriptor );
        assertEquals( 1, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        Standard standard = new Standard();
        standard.setValue1( "Foo" );
        standard.setValue2( 321 );
        out.writeObject( standard );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        Standard result = (Standard) in.readObject();

        assertNull( "value1 must not be set", result.getValue1() );
        assertEquals( standard.getValue2(), result.getValue2() );
    }

    @Test
    public void testStandardPropertyFindUsingExcludes()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( Inherted.class ).attributes().excludes( "value1", "value2" );
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( Inherted.class );

        assertNotNull( classDescriptor );
        assertEquals( 2, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        Inherted inherted = new Inherted();
        inherted.setValue1( "Foo" );
        inherted.setValue2( 321 );
        inherted.setValue3( "Bar" );
        inherted.setValue4( 123L );
        out.writeObject( inherted );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        Inherted result = (Inherted) in.readObject();

        assertNull( "value1 must not be set", result.getValue1() );
        assertEquals( result.getValue2(), 0 );
        assertEquals( inherted.getValue3(), result.getValue3() );
        assertEquals( inherted.getValue4(), result.getValue4() );
    }

    @Test
    public void testInheritancePropertyFind()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( Inherted.class ).attributes();
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( Inherted.class );

        assertNotNull( classDescriptor );
        assertEquals( 4, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        Inherted inherted = new Inherted();
        inherted.setValue1( "Foo" );
        inherted.setValue2( 321 );
        inherted.setValue3( "Bar" );
        inherted.setValue4( 123 );
        out.writeObject( inherted );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        Standard result = (Standard) in.readObject();

        assertEquals( inherted, result );
    }

    @Test
    public void testCompositionPropertyFind()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( Composed.class ).attributes();
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( Composed.class );

        assertNotNull( classDescriptor );
        assertEquals( 3, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        Composed composed = new Composed( "Foo", 123, "Bar" );
        out.writeObject( composed );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        Composed result = (Composed) in.readObject();

        assertEquals( composed, result );
    }

    @Test
    public void testComposedInheritancePropertyFind()
        throws Exception
    {
        Serializer serializer = Lightning.createSerializer( new AbstractSerializerDefinition()
        {

            @Override
            protected void configure()
            {
                serialize( ComposedInherted.class ).attributes();
            }
        } );

        ClassDescriptorAwareSerializer awareSerializer = (ClassDescriptorAwareSerializer) serializer;
        ClassDescriptor classDescriptor = awareSerializer.findClassDescriptor( ComposedInherted.class );

        assertNotNull( classDescriptor );
        assertEquals( 3, classDescriptor.getPropertyDescriptors().size() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );

        ComposedInherted composedInherted = new ComposedInherted();
        composedInherted.setValue1( "Foo" );
        composedInherted.setValue2( 321 );
        composedInherted.setValue3( "Bar" );
        out.writeObject( composedInherted );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );

        ComposedInherted result = (ComposedInherted) in.readObject();

        assertEquals( composedInherted, result );
    }

    public static class Standard
    {

        @Attribute
        private String value1;

        @Attribute
        private int value2;

        public String getValue1()
        {
            return value1;
        }

        public void setValue1( String value1 )
        {
            this.value1 = value1;
        }

        public int getValue2()
        {
            return value2;
        }

        public void setValue2( int value2 )
        {
            this.value2 = value2;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( value1 == null ) ? 0 : value1.hashCode() );
            result = prime * result + value2;
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
            Standard other = (Standard) obj;
            if ( value1 == null )
            {
                if ( other.value1 != null )
                    return false;
            }
            else if ( !value1.equals( other.value1 ) )
                return false;
            if ( value2 != other.value2 )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "Standard [value1=" + value1 + ", value2=" + value2 + "]";
        }
    }

    public static class Inherted
        extends Standard
    {

        @Attribute
        private String value3;

        @Attribute
        private long value4;

        public String getValue3()
        {
            return value3;
        }

        public void setValue3( String value3 )
        {
            this.value3 = value3;
        }

        public long getValue4()
        {
            return value4;
        }

        public void setValue4( long value4 )
        {
            this.value4 = value4;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ( ( value3 == null ) ? 0 : value3.hashCode() );
            result = prime * result + (int) ( value4 ^ ( value4 >>> 32 ) );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( !super.equals( obj ) )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            Inherted other = (Inherted) obj;
            if ( value3 == null )
            {
                if ( other.value3 != null )
                    return false;
            }
            else if ( !value3.equals( other.value3 ) )
                return false;
            if ( value4 != other.value4 )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "Inherted [value3=" + value3 + ", value4=" + value4 + "]";
        }
    }

    public static class ComposedInherted
        extends Standard
        implements Foo, Bar
    {

        private String value3;

        public void setValue3( String value3 )
        {
            this.value3 = value3;
        }

        @Override
        public String getValue3()
        {
            return value3;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ( ( value3 == null ) ? 0 : value3.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( !super.equals( obj ) )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ComposedInherted other = (ComposedInherted) obj;
            if ( value3 == null )
            {
                if ( other.value3 != null )
                    return false;
            }
            else if ( !value3.equals( other.value3 ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "ComposedInherted [value3=" + value3 + "]";
        }
    }

    public static interface Foo
    {

        @Attribute
        String getValue1();

        @Attribute
        int getValue2();
    }

    public static interface Bar
    {

        @Attribute
        String getValue3();
    }

    public static class Composed
        implements Foo, Bar
    {

        private final String value1;

        private final int value2;

        private final String value3;

        public Composed( String value1, int value2, String value3 )
        {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
        }

        @Override
        public String getValue3()
        {
            return value3;
        }

        @Override
        public String getValue1()
        {
            return value1;
        }

        @Override
        public int getValue2()
        {
            return value2;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( value1 == null ) ? 0 : value1.hashCode() );
            result = prime * result + value2;
            result = prime * result + ( ( value3 == null ) ? 0 : value3.hashCode() );
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
            Composed other = (Composed) obj;
            if ( value1 == null )
            {
                if ( other.value1 != null )
                    return false;
            }
            else if ( !value1.equals( other.value1 ) )
                return false;
            if ( value2 != other.value2 )
                return false;
            if ( value3 == null )
            {
                if ( other.value3 != null )
                    return false;
            }
            else if ( !value3.equals( other.value3 ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "Composed [value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + "]";
        }
    }
}
