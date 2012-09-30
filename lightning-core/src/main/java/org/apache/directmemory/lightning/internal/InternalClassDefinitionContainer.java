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
package org.apache.directmemory.lightning.internal;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directmemory.lightning.internal.util.ClassUtil;
import org.apache.directmemory.lightning.metadata.ClassDefinition;
import org.apache.directmemory.lightning.metadata.ClassDefinitionContainer;

import com.carrotsearch.hppc.LongObjectMap;
import com.carrotsearch.hppc.LongObjectOpenHashMap;

class InternalClassDefinitionContainer
    implements ClassDefinitionContainer, Serializable
{

    private static final long serialVersionUID = -8496850178968208567L;

    private final ClassDefinition[] classDefinitions;

    private final LongObjectMap<ClassDefinition> classDefinitionsMappings;

    // Serialization
    private InternalClassDefinitionContainer( ClassDefinition[] classDefinitions )
    {
        this.classDefinitions = classDefinitions;
        this.classDefinitionsMappings = new LongObjectOpenHashMap<ClassDefinition>();
    }

    InternalClassDefinitionContainer( Set<ClassDefinition> classDefinitions )
    {
        this.classDefinitions = classDefinitions.toArray( new ClassDefinition[classDefinitions.size()] );
        this.classDefinitionsMappings = new LongObjectOpenHashMap<ClassDefinition>( classDefinitions.size() );
        initMappings( this.classDefinitions );
    }

    @Override
    public Collection<ClassDefinition> getClassDefinitions()
    {
        return Arrays.asList( Arrays.copyOf( classDefinitions, classDefinitions.length ) );
    }

    @Override
    public Class<?> getTypeById( long id )
    {
        ClassDefinition classDefinition = classDefinitionsMappings.get( id );
        return classDefinition != null ? classDefinition.getType() : null;
    }

    @Override
    public ClassDefinition getClassDefinitionByCanonicalName( String canonicalName )
    {
        for ( ClassDefinition classDefinition : classDefinitions )
        {
            if ( classDefinition.getCanonicalName().equals( canonicalName ) )
            {
                return classDefinition;
            }
        }
        return null;
    }

    @Override
    public ClassDefinition getClassDefinitionById( long id )
    {
        ClassDefinition classDefinition = classDefinitionsMappings.get( id );
        return classDefinition != null ? classDefinition : null;
    }

    @Override
    public ClassDefinition getClassDefinitionByType( Class<?> type )
    {
        if ( List.class.isAssignableFrom( type ) )
        {
            type = List.class;
        }
        else if ( Set.class.isAssignableFrom( type ) )
        {
            type = Set.class;
        }
        else if ( Map.class.isAssignableFrom( type ) )
        {
            type = Map.class;
        }

        for ( ClassDefinition classDefinition : classDefinitions )
        {
            if ( classDefinition.getType() == type )
            {
                return classDefinition;
            }
        }
        return null;
    }

    private void initMappings( ClassDefinition[] classDefinitions )
    {
        for ( ClassDefinition classDefinition : classDefinitions )
        {
            classDefinitionsMappings.put( classDefinition.getId(), classDefinition );
        }
    }

    Object writeReplace()
    {
        return new InternalClassDefinitionProxy( this );
    }

    /**
     * SerializationProxy
     */
    private static class InternalClassDefinitionProxy
        implements Externalizable
    {

        private static final long serialVersionUID = 3127589236225504001L;

        private final InternalClassDefinitionContainer classDefinitionContainer;

        private ClassDefinition[] classDefinitions;

        @SuppressWarnings( "unused" )
        public InternalClassDefinitionProxy()
        {
            this.classDefinitionContainer = null;
        }

        private InternalClassDefinitionProxy( InternalClassDefinitionContainer classDefinitionContainer )
        {
            this.classDefinitionContainer = classDefinitionContainer;
        }

        @Override
        public void writeExternal( ObjectOutput out )
            throws IOException
        {
            List<ClassDefinition> selectedClassDefinitions = new ArrayList<ClassDefinition>();
            for ( ClassDefinition classDefinition : classDefinitionContainer.classDefinitions )
            {
                if ( classDefinition.getId() < 1000 )
                {
                    continue;
                }

                selectedClassDefinitions.add( classDefinition );
            }

            out.writeInt( selectedClassDefinitions.size() );
            for ( ClassDefinition classDefinition : selectedClassDefinitions )
            {
                final long id = classDefinition.getId();
                final byte[] checksum = classDefinition.getChecksum();
                final String canonicalName = classDefinition.getCanonicalName();
                final long serialVersionUID = classDefinition.getSerialVersionUID();

                out.writeLong( id );
                out.writeUTF( canonicalName );
                out.write( checksum );
                out.writeLong( serialVersionUID );
            }
        }

        @Override
        public void readExternal( ObjectInput in )
            throws IOException, ClassNotFoundException
        {
            int size = in.readInt();

            classDefinitions = new ClassDefinition[size + ClassUtil.CLASS_DESCRIPTORS.length];
            for ( int i = 0; i < ClassUtil.CLASS_DESCRIPTORS.length; i++ )
            {
                classDefinitions[i] = ClassUtil.CLASS_DESCRIPTORS[i];
            }

            for ( int i = 0; i < size; i++ )
            {
                final long id = in.readLong();
                final String canonicalName = in.readUTF();
                final byte[] checksum = new byte[20];
                in.readFully( checksum );
                final long serialVersionUID = in.readLong();

                try
                {
                    Class<?> type = ClassUtil.loadClass( canonicalName );
                    classDefinitions[i + ClassUtil.CLASS_DESCRIPTORS.length] =
                        new InternalClassDefinition( id, type, checksum, serialVersionUID );
                }
                catch ( ClassNotFoundException e )
                {
                    throw new IOException( "Class " + canonicalName + " could not be loaded", e );
                }
            }
        }

        private Object readResolve()
        {
            InternalClassDefinitionContainer classDefinitionContainer =
                new InternalClassDefinitionContainer( classDefinitions );
            classDefinitionContainer.initMappings( classDefinitions );
            return classDefinitionContainer;
        }
    }
}
