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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.SerializationContext;
import org.apache.directmemory.lightning.TypeBindableMarshaller;
import org.apache.directmemory.lightning.base.AbstractMarshaller;
import org.apache.directmemory.lightning.exceptions.SerializerExecutionException;
import org.apache.directmemory.lightning.internal.CheatPropertyDescriptor;
import org.apache.directmemory.lightning.internal.util.TypeUtil;
import org.apache.directmemory.lightning.metadata.ClassDefinition;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;

public class SetMarshaller
    extends AbstractMarshaller
    implements TypeBindableMarshaller
{

    private final Type setType;

    private Marshaller setTypeMarshaller;

    public SetMarshaller()
    {
        this( null );
    }

    private SetMarshaller( Type setType )
    {
        this.setType = setType;
    }

    @Override
    public boolean acceptType( Class<?> type )
    {
        return Set.class.isAssignableFrom( type );
    }

    @Override
    public void marshall( Object value, PropertyDescriptor propertyDescriptor, DataOutput dataOutput,
                          SerializationContext serializationContext )
        throws IOException
    {

        if ( writePossibleNull( value, dataOutput ) )
        {
            Set<?> set = (Set<?>) value;
            dataOutput.writeInt( set.size() );

            Marshaller marshaller = null;
            ClassDefinition classDefinition = null;
            PropertyDescriptor pd = null;
            if ( setType != null )
            {
                ensureMarshallerInitialized( serializationContext );
                marshaller = setTypeMarshaller;
                Class<?> baseType = TypeUtil.getBaseType( setType );
                classDefinition =
                    serializationContext.getClassDefinitionContainer().getClassDefinitionByType( baseType );
                pd = new CheatPropertyDescriptor( propertyDescriptor.getPropertyName() + "Set", baseType, marshaller );
            }

            for ( Object entry : set )
            {
                if ( writePossibleNull( entry, dataOutput ) )
                {
                    if ( setType == null )
                    {
                        marshaller = serializationContext.findMarshaller( entry.getClass() );
                        classDefinition =
                            serializationContext.getClassDefinitionContainer().getClassDefinitionByType( entry.getClass() );
                        pd =
                            new CheatPropertyDescriptor( propertyDescriptor.getPropertyName() + "Set",
                                                         entry.getClass(), marshaller );
                    }

                    dataOutput.writeLong( classDefinition.getId() );
                    marshaller.marshall( entry, pd, dataOutput, serializationContext );
                }
            }
        }
    }

    @Override
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public <V> V unmarshall( PropertyDescriptor propertyDescriptor, DataInput dataInput,
                             SerializationContext serializationContext )
        throws IOException
    {
        if ( isNull( dataInput ) )
        {
            return null;
        }

        int size = dataInput.readInt();
        Set set = new HashSet( size );
        if ( size > 0 )
        {
            for ( int i = 0; i < size; i++ )
            {
                if ( isNull( dataInput ) )
                {
                    set.add( null );
                }
                else
                {
                    long classId = dataInput.readLong();
                    ClassDefinition classDefinition =
                        serializationContext.getClassDefinitionContainer().getClassDefinitionById( classId );

                    Marshaller marshaller;
                    if ( setType != null )
                    {
                        ensureMarshallerInitialized( serializationContext );
                        marshaller = setTypeMarshaller;
                    }
                    else
                    {
                        marshaller = serializationContext.findMarshaller( classDefinition.getType() );
                    }

                    PropertyDescriptor pd =
                        new CheatPropertyDescriptor( propertyDescriptor.getPropertyName() + "Set",
                                                     classDefinition.getType(), marshaller );
                    set.add( marshaller.unmarshall( pd, dataInput, serializationContext ) );
                }
            }
        }

        return (V) set;
    }

    @Override
    public Marshaller bindType( Type... bindingTypes )
    {
        if ( bindingTypes == null )
        {
            return new SetMarshaller();
        }

        if ( bindingTypes.length != 1 )
        {
            throw new SerializerExecutionException( "Set type binding has no single generic: "
                + Arrays.toString( bindingTypes ) );
        }

        Type setType = bindingTypes[0];
        return new SetMarshaller( setType );
    }

    private void ensureMarshallerInitialized( SerializationContext serializationContext )
    {
        if ( setTypeMarshaller != null )
            return;

        setTypeMarshaller = serializationContext.findMarshaller( setType );
    }
}
