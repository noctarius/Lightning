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

import org.apache.directmemory.lightning.SerializationContext;
import org.apache.directmemory.lightning.base.AbstractMarshaller;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;

public class FloatArrayMarshaller
    extends AbstractMarshaller
{

    @Override
    public boolean acceptType( Class<?> type )
    {
        return float[].class == type || Float[].class == type;
    }

    @Override
    public void marshall( Object value, PropertyDescriptor propertyDescriptor, DataOutput dataOutput,
                          SerializationContext serializationContext )
        throws IOException
    {

        if ( !writePossibleNull( value, dataOutput ) )
        {
            return;
        }

        if ( float[].class == propertyDescriptor.getType() )
        {
            float[] array = (float[]) value;
            dataOutput.writeInt( array.length );

            for ( float arrayValue : array )
            {
                dataOutput.writeFloat( arrayValue );
            }
        }
        else
        {
            Float[] array = (Float[]) value;
            dataOutput.writeInt( array.length );

            for ( float arrayValue : array )
            {
                dataOutput.writeFloat( arrayValue );
            }
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <V> V unmarshall( PropertyDescriptor propertyDescriptor, DataInput dataInput,
                             SerializationContext serializationContext )
        throws IOException
    {
        if ( isNull( dataInput ) )
        {
            return null;
        }

        int size = dataInput.readInt();
        if ( float[].class == propertyDescriptor.getType() )
        {
            float[] array = new float[size];
            for ( int i = 0; i < size; i++ )
            {
                array[i] = dataInput.readFloat();
            }

            return (V) array;
        }
        else
        {
            Float[] array = new Float[size];
            for ( int i = 0; i < size; i++ )
            {
                array[i] = dataInput.readFloat();
            }

            return (V) array;
        }
    }
}
