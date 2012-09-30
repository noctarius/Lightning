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

import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.MarshallerContext;
import org.apache.directmemory.lightning.MarshallerStrategy;
import org.apache.directmemory.lightning.SerializationContext;
import org.apache.directmemory.lightning.SerializationStrategy;
import org.apache.directmemory.lightning.TypeBindableMarshaller;
import org.apache.directmemory.lightning.instantiator.ObjectInstantiatorFactory;
import org.apache.directmemory.lightning.internal.util.TypeUtil;
import org.apache.directmemory.lightning.metadata.ClassDefinitionContainer;
import org.apache.directmemory.lightning.metadata.ValueNullableEvaluator;

import com.carrotsearch.hppc.LongObjectMap;
import com.carrotsearch.hppc.LongObjectOpenHashMap;

public class InternalSerializationContext
    implements SerializationContext
{

    private final Map<Object, Long> referencesMarshall;

    private final LongObjectMap<Object> referencesUnmarshall;

    private final MarshallerContext marshallerContext = new InternalMarshallerContext();

    private final ClassDefinitionContainer classDefinitionContainer;

    private final SerializationStrategy serializationStrategy;

    private final MarshallerStrategy marshallerStrategy;

    private final ObjectInstantiatorFactory objectInstantiatorFactory;

    private final ValueNullableEvaluator valueNullableEvaluator;

    private long nextReferenceIdMarshall = 10000;

    public InternalSerializationContext( ClassDefinitionContainer classDefinitionContainer,
                                         SerializationStrategy serializationStrategy,
                                         MarshallerStrategy marshallerStrategy,
                                         ObjectInstantiatorFactory objectInstantiatorFactory,
                                         ValueNullableEvaluator valueNullableEvaluator,
                                         Map<Class<?>, Marshaller> definedMarshallers )
    {

        this.classDefinitionContainer = classDefinitionContainer;
        this.serializationStrategy = serializationStrategy;
        this.marshallerStrategy = marshallerStrategy;
        this.objectInstantiatorFactory = objectInstantiatorFactory;
        this.valueNullableEvaluator = valueNullableEvaluator;

        for ( Entry<Class<?>, Marshaller> entry : definedMarshallers.entrySet() )
        {
            this.marshallerContext.bindMarshaller( entry.getKey(), entry.getValue() );
        }

        if ( serializationStrategy == SerializationStrategy.SizeOptimized )
        {
            this.referencesMarshall = new IdentityHashMap<Object, Long>();
            this.referencesUnmarshall = new LongObjectOpenHashMap<Object>();
        }
        else
        {
            this.referencesMarshall = null;
            this.referencesUnmarshall = null;
        }
    }

    @Override
    public ClassDefinitionContainer getClassDefinitionContainer()
    {
        return classDefinitionContainer;
    }

    @Override
    public SerializationStrategy getSerializationStrategy()
    {
        return serializationStrategy;
    }

    @Override
    public ObjectInstantiatorFactory getObjectInstantiatorFactory()
    {
        return objectInstantiatorFactory;
    }

    @Override
    public long findReferenceIdByObject( Object instance )
    {
        Long referenceId = referencesMarshall.get( instance );
        if ( referenceId == null )
        {
            return -1;
        }
        return referenceId;
    }

    @Override
    public long putMarshalledInstance( Object instance )
    {
        long newId = getNextReferenceIdMarshall();
        referencesMarshall.put( instance, newId );
        return newId;
    }

    @Override
    public Object findObjectByReferenceId( long referenceId )
    {
        return referencesUnmarshall.get( referenceId );
    }

    @Override
    public boolean containsReferenceId( long referenceId )
    {
        return referencesUnmarshall.containsKey( referenceId );
    }

    @Override
    public long putUnmarshalledInstance( long refrenceId, Object instance )
    {
        referencesUnmarshall.put( refrenceId, instance );
        return refrenceId;
    }

    @Override
    public Marshaller findMarshaller( Type type )
    {
        Class<?> rawType = TypeUtil.getBaseType( type );
        Marshaller marshaller = marshallerStrategy.getMarshaller( rawType, marshallerContext, false );

        if ( marshaller instanceof TypeBindableMarshaller )
        {
            Type[] typeArguments = TypeUtil.getTypeArgument( type );
            marshaller = ( (TypeBindableMarshaller) marshaller ).bindType( typeArguments );
        }

        return marshaller;
    }

    public Map<Object, Long> getReferencesMarshall()
    {
        return referencesMarshall;
    }

    public LongObjectMap<Object> getReferencesUnmarshall()
    {
        return referencesUnmarshall;
    }

    public long getNextReferenceIdMarshall()
    {
        long newId = nextReferenceIdMarshall++;
        return newId;
    }

    @Override
    public ValueNullableEvaluator getValueNullableEvaluator()
    {
        return valueNullableEvaluator;
    }
}
