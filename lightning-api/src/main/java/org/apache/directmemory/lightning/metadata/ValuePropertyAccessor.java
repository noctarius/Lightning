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

public interface ValuePropertyAccessor
    extends PropertyAccessor
{

    <T> void writeObject( Object instance, T value );

    <T> T readObject( Object instance );

    void writeBoolean( Object instance, boolean value );

    boolean readBoolean( Object instance );

    void writeByte( Object instance, byte value );

    byte readByte( Object instance );

    void writeChar( Object instance, char value );

    char readChar( Object instance );

    void writeShort( Object instance, short value );

    short readShort( Object instance );

    void writeInt( Object instance, int value );

    int readInt( Object instance );

    void writeLong( Object instance, long value );

    long readLong( Object instance );

    void writeFloat( Object instance, float value );

    float readFloat( Object instance );

    void writeDouble( Object instance, double value );

    double readDouble( Object instance );

}
