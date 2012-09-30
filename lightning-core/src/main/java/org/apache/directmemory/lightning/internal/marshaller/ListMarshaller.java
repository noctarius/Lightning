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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.SerializationContext;
import org.apache.directmemory.lightning.TypeBindableMarshaller;
import org.apache.directmemory.lightning.base.AbstractMarshaller;
import org.apache.directmemory.lightning.exceptions.SerializerExecutionException;
import org.apache.directmemory.lightning.internal.CheatPropertyDescriptor;
import org.apache.directmemory.lightning.internal.util.TypeUtil;
import org.apache.directmemory.lightning.metadata.ClassDefinition;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;

public class ListMarshaller
    extends AbstractMarshaller
    implements TypeBindableMarshaller
{

    private final Type listType;

    private Marshaller listTypeMarshaller;

    public ListMarshaller()
    {
        this( null );
    }

    private ListMarshaller( Type listType )
    {
        this.listType = listType;
    }

    @Override
    public boolean acceptType( Class<?> type )
    {
        return List.class.isAssignableFrom( type );
    }

    @Override
    public void marshall( Object value, PropertyDescriptor propertyDescriptor, DataOutput dataOutput,
                          SerializationContext serializationContext )
        throws IOException
    {

        if ( writePossibleNull( value, dataOutput ) )
        {
            List<?> list = (List<?>) value;
            dataOutput.writeInt( list.size() );

            Marshaller marshaller = null;
            ClassDefinition classDefinition = null;
            PropertyDescriptor pd = null;
            if ( listType != null )
            {
                ensureMarshallerInitialized( serializationContext );
                marshaller = listTypeMarshaller;
                Class<?> baseType = TypeUtil.getBaseType( listType );
                classDefinition =
                    serializationContext.getClassDefinitionContainer().getClassDefinitionByType( baseType );
                pd = new CheatPropertyDescriptor( propertyDescriptor.getPropertyName() + "List", baseType, marshaller );
            }

            for ( Object entry : list )
            {
                if ( writePossibleNull( entry, dataOutput ) )
                {
                    if ( listType == null )
                    {
                        marshaller = serializationContext.findMarshaller( entry.getClass() );
                        classDefinition =
                            serializationContext.getClassDefinitionContainer().getClassDefinitionByType( entry.getClass() );
                        pd =
                            new CheatPropertyDescriptor( propertyDescriptor.getPropertyName() + "List",
                                                         entry.getClass(), marshaller );
                    }

                    if ( classDefinition == null )
                    {
                        throw new SerializerExecutionException( "No ClassDefinition found for type " + entry.getClass() );
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
        List list = new ArrayList( size );
        if ( size > 0 )
        {
            for ( int i = 0; i < size; i++ )
            {
                if ( isNull( dataInput ) )
                {
                    list.add( null );
                }
                else
                {
                    long classId = dataInput.readLong();
                    ClassDefinition classDefinition =
                        serializationContext.getClassDefinitionContainer().getClassDefinitionById( classId );

                    Marshaller marshaller;
                    if ( listType != null )
                    {
                        ensureMarshallerInitialized( serializationContext );
                        marshaller = listTypeMarshaller;
                    }
                    else
                    {
                        marshaller = serializationContext.findMarshaller( classDefinition.getType() );
                    }

                    PropertyDescriptor pd =
                        new CheatPropertyDescriptor( propertyDescriptor.getPropertyName() + "List",
                                                     classDefinition.getType(), marshaller );
                    list.add( marshaller.unmarshall( pd, dataInput, serializationContext ) );
                }
            }
        }

        return (V) list;
    }

    @Override
    public Marshaller bindType( Type... bindingTypes )
    {
        if ( bindingTypes == null )
        {
            return new ListMarshaller();
        }

        if ( bindingTypes.length != 1 )
        {
            throw new SerializerExecutionException( "List type binding has no single generic: "
                + Arrays.toString( bindingTypes ) );
        }

        Type listType = bindingTypes[0];
        return new ListMarshaller( listType );
    }

    private void ensureMarshallerInitialized( SerializationContext serializationContext )
    {
        if ( listTypeMarshaller != null )
            return;

        listTypeMarshaller = serializationContext.findMarshaller( listType );
    }
}
