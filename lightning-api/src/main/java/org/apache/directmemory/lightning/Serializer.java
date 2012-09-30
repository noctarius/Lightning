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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;

import org.apache.directmemory.lightning.metadata.ClassDefinitionContainer;

public interface Serializer
{

    ClassDefinitionContainer getClassDefinitionContainer();

    void setClassDefinitionContainer( ClassDefinitionContainer classDefinitionContainer );

    <V> void serialize( V value, DataOutput dataOutput );

    <V> void serialize( V value, OutputStream outputStream );

    <V> void serialize( V value, Writer writer );

    <V> void serialize( V value, ByteBuffer buffer );

    <V> V deserialize( DataInput dataInput );

    <V> V deserialize( InputStream inputStream );

    <V> V deserialize( Reader reader );

    <V> V deserialize( ByteBuffer buffer );

}
