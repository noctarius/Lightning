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
package org.apache.directmemory.lightning.metadata;

public interface ArrayPropertyAccessor
    extends ValuePropertyAccessor
{

    <T> void writeObject( Object instance, int index, T value );

    <T> T readObject( Object instance, int index );

    void writeBoolean( Object instance, int index, boolean value );

    boolean readBoolean( Object instance, int index );

    void writeByte( Object instance, int index, byte value );

    byte readByte( Object instance, int index );

    void writeChar( Object instance, int index, char value );

    char readChar( Object instance, int index );

    void writeShort( Object instance, int index, short value );

    short readShort( Object instance, int index );

    void writeInt( Object instance, int index, int value );

    int readInt( Object instance, int index );

    void writeLong( Object instance, int index, long value );

    long readLong( Object instance, int index );

    void writeFloat( Object instance, int index, float value );

    float readFloat( Object instance, int index );

    void writeDouble( Object instance, int index, double value );

    double readDouble( Object instance, int index );
}
