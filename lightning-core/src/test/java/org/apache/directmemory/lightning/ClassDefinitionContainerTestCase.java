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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directmemory.lightning.ClassComparisonStrategy;
import org.apache.directmemory.lightning.Lightning;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.base.AbstractSerializerDefinition;
import org.apache.directmemory.lightning.exceptions.ClassDefinitionInconsistentException;
import org.apache.directmemory.lightning.internal.util.DebugLogger;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.apache.directmemory.lightning.metadata.ClassDefinitionContainer;
import org.junit.Test;

public class ClassDefinitionContainerTestCase
{

    @Test
    public void testLightningChecksum()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).debugCacheDirectory( new File( "target" ) ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        Serializer remoteSerializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new SerializerDefinition() ).build();

        remoteSerializer.setClassDefinitionContainer( container );
    }

    @Test( expected = ClassDefinitionInconsistentException.class )
    public void testLightningChecksumFailing()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        Serializer remoteSerializer = Lightning.newBuilder().logger( new DebugLogger() ).build();

        remoteSerializer.setClassDefinitionContainer( container );
    }

    @Test
    public void testSerialVersionUID()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).classComparisonStrategy( ClassComparisonStrategy.SerialVersionUID ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        Serializer remoteSerializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new SerializerDefinition() ).build();

        remoteSerializer.setClassDefinitionContainer( container );
    }

    @Test( expected = ClassDefinitionInconsistentException.class )
    public void testSerialVersionUIDFailing()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).classComparisonStrategy( ClassComparisonStrategy.SerialVersionUID ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        Serializer remoteSerializer = Lightning.newBuilder().logger( new DebugLogger() ).build();

        remoteSerializer.setClassDefinitionContainer( container );
    }

    @Test
    public void testClassDefinitionContainerTransportLightningChecksum()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( container );
        byte[] data = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream( data );
        ObjectInputStream in = new ObjectInputStream( bais );

        ClassDefinitionContainer remoteContainer = (ClassDefinitionContainer) in.readObject();

        Serializer remoteSerializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new SerializerDefinition() ).build();

        remoteSerializer.setClassDefinitionContainer( remoteContainer );
    }

    @Test
    public void testClassDefinitionContainerTransportSerialVersionUID()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).classComparisonStrategy( ClassComparisonStrategy.SerialVersionUID ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( container );
        byte[] data = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream( data );
        ObjectInputStream in = new ObjectInputStream( bais );

        ClassDefinitionContainer remoteContainer = (ClassDefinitionContainer) in.readObject();

        Serializer remoteSerializer =
            Lightning.newBuilder().logger( new DebugLogger() ).classComparisonStrategy( ClassComparisonStrategy.SerialVersionUID ).serializerDefinitions( new SerializerDefinition() ).build();

        remoteSerializer.setClassDefinitionContainer( remoteContainer );
    }

    public static class SerializerDefinition
        extends AbstractSerializerDefinition
    {

        @Override
        protected void configure()
        {
            serialize( Foo.class ).attributes();
            serialize( Bar.class ).attributes();
        }
    }

    public static class Foo
    {

        @Attribute
        private int id;

        @Attribute
        private String name;

        public int getId()
        {
            return id;
        }

        public void setId( int id )
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }
    }

    public static class Bar
    {

        @Attribute
        private int id;

        @Attribute
        private String name;

        public int getId()
        {
            return id;
        }

        public void setId( int id )
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }
    }
}
