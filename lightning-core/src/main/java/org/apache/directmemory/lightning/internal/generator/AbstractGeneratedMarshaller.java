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
package org.apache.directmemory.lightning.internal.generator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.SerializationContext;
import org.apache.directmemory.lightning.SerializationStrategy;
import org.apache.directmemory.lightning.exceptions.SerializerDefinitionException;
import org.apache.directmemory.lightning.instantiator.ObjectInstantiator;
import org.apache.directmemory.lightning.instantiator.ObjectInstantiatorFactory;
import org.apache.directmemory.lightning.internal.ClassDescriptorAwareSerializer;
import org.apache.directmemory.lightning.internal.util.ClassUtil;
import org.apache.directmemory.lightning.metadata.ClassDescriptor;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;

public abstract class AbstractGeneratedMarshaller
    implements Marshaller
{

    private final Class<?> clazz;

    private final Map<Class<?>, Marshaller> marshallers;

    private final ClassDescriptor classDescriptor;

    private final List<PropertyDescriptor> propertyDescriptors;

    private final ObjectInstantiator objectInstantiator;

    public AbstractGeneratedMarshaller( Class<?> clazz, Map<Class<?>, Marshaller> marshallers,
                                        ClassDescriptorAwareSerializer serializer,
                                        ObjectInstantiatorFactory objectInstantiatorFactory )
    {

        this.clazz = clazz;
        this.marshallers = marshallers;
        this.classDescriptor = serializer.findClassDescriptor( clazz );
        this.propertyDescriptors = Collections.unmodifiableList( classDescriptor.getPropertyDescriptors() );
        this.objectInstantiator = objectInstantiatorFactory.getInstantiatorOf( clazz );
    }

    @Override
    public boolean acceptType( Class<?> type )
    {
        return clazz.isAssignableFrom( type );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <V> V unmarshall( PropertyDescriptor propertyDescriptor, DataInput dataInput,
                             SerializationContext serializationContext )
        throws IOException
    {
        if ( serializationContext.getSerializationStrategy() == SerializationStrategy.SizeOptimized )
        {
            if ( ClassUtil.isReferenceCapable( propertyDescriptor.getType() ) )
            {
                long referenceId = dataInput.readLong();
                V instance;
                if ( containsReferenceId( referenceId, serializationContext ) )
                {
                    instance = (V) findObjectByReferenceId( referenceId, serializationContext );
                }
                else
                {
                    // Instance not yet received, for first time deserialize it
                    instance = unmarshall( (V) newInstance(), propertyDescriptor, dataInput, serializationContext );
                    cacheObjectForUnmarshall( referenceId, instance, serializationContext );
                }

                return instance;
            }
        }

        V value = null;
        if ( !propertyDescriptor.getType().isArray() )
        {
            value = (V) newInstance();
        }

        return unmarshall( value, propertyDescriptor, dataInput, serializationContext );
    }

    protected abstract <V> V unmarshall( V value, PropertyDescriptor propertyDescriptor, DataInput dataInput,
                                         SerializationContext serializationContext )
        throws IOException;

    protected boolean isAlreadyMarshalled( Object value, Class<?> type, DataOutput dataOutput,
                                           SerializationContext serializationContext )
        throws IOException
    {
        if ( serializationContext.getSerializationStrategy() != SerializationStrategy.SizeOptimized )
        {
            return false;
        }

        if ( !ClassUtil.isReferenceCapable( type ) )
        {
            return false;
        }

        long referenceId = findReferenceIdByObject( value, serializationContext );
        if ( referenceId == -1 )
        {
            referenceId = cacheObjectForMarshall( value, serializationContext );
            dataOutput.writeLong( referenceId );
            return false;
        }

        dataOutput.writeLong( referenceId );
        return true;
    }

    protected ClassDescriptor getClassDescriptor()
    {
        return classDescriptor;
    }

    protected Object newInstance()
    {
        return objectInstantiator.newInstance();
    }

    protected PropertyDescriptor getPropertyDescriptor( String propertyName )
    {
        for ( PropertyDescriptor propertyDescriptor : propertyDescriptors )
        {
            if ( propertyDescriptor.getPropertyName().equals( propertyName ) )
            {
                return propertyDescriptor;
            }
        }

        // This should never happen
        return null;
    }

    protected PropertyAccessor getPropertyAccessor( String propertyName )
    {
        return getPropertyDescriptor( propertyName ).getPropertyAccessor();
    }

    protected Marshaller findMarshaller( PropertyDescriptor propertyDescriptor )
    {
        if ( propertyDescriptor.getMarshaller() != null )
        {
            return propertyDescriptor.getMarshaller();
        }

        Marshaller marshaller = marshallers.get( propertyDescriptor.getType() );
        if ( marshaller != null )
        {
            return marshaller;
        }

        return new DelegatingMarshaller( propertyDescriptor );
    }

    protected long findReferenceIdByObject( Object instance, SerializationContext serializationContext )
    {
        return serializationContext.findReferenceIdByObject( instance );
    }

    protected Object findObjectByReferenceId( long referenceId, SerializationContext serializationContext )
    {
        return serializationContext.findObjectByReferenceId( referenceId );
    }

    protected boolean containsReferenceId( long referenceId, SerializationContext serializationContext )
    {
        return serializationContext.containsReferenceId( referenceId );
    }

    protected long cacheObjectForMarshall( Object instance, SerializationContext serializationContext )
    {
        return serializationContext.putMarshalledInstance( instance );
    }

    protected long cacheObjectForUnmarshall( long referenceId, Object instance,
                                             SerializationContext serializationContext )
    {
        return serializationContext.putUnmarshalledInstance( referenceId, instance );
    }

    private class DelegatingMarshaller
        implements Marshaller
    {

        private final PropertyDescriptor marshalledProperty;

        private Marshaller marshaller;

        private DelegatingMarshaller( PropertyDescriptor marshalledProperty )
        {
            this.marshalledProperty = marshalledProperty;
        }

        @Override
        public boolean acceptType( Class<?> type )
        {
            return marshalledProperty.getType().isAssignableFrom( type );
        }

        @Override
        public void marshall( Object value, PropertyDescriptor propertyDescriptor, DataOutput dataOutput,
                              SerializationContext serializationContext )
            throws IOException
        {

            Marshaller marshaller = this.marshaller;
            if ( marshaller == null )
            {
                marshaller = getMarshaller();
            }

            if ( marshaller == null )
            {
                throw new SerializerDefinitionException( "No marshaller for property " + marshalledProperty + " found" );
            }

            marshaller.marshall( value, propertyDescriptor, dataOutput, serializationContext );
        }

        @Override
        public <V> V unmarshall( PropertyDescriptor propertyDescriptor, DataInput dataInput,
                                 SerializationContext serializationContext )
            throws IOException
        {
            Marshaller marshaller = this.marshaller;
            if ( marshaller == null )
            {
                marshaller = getMarshaller();
            }

            if ( marshaller == null )
            {
                throw new SerializerDefinitionException( "No marshaller for property " + marshalledProperty + " found" );
            }

            return marshaller.unmarshall( propertyDescriptor, dataInput, serializationContext );
        }

        private synchronized Marshaller getMarshaller()
        {
            if ( marshaller == null )
            {
                marshaller = findMarshaller( marshalledProperty );
            }
            return marshaller;
        }

    }
}
