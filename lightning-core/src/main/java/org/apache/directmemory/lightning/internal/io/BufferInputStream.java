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
package org.apache.directmemory.lightning.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferInputStream
    extends InputStream
{

    private final ByteBuffer byteBuffer;

    public BufferInputStream( ByteBuffer byteBuffer )
    {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public synchronized int read()
        throws IOException
    {
        if ( !byteBuffer.hasRemaining() )
        {
            return -1;
        }
        return byteBuffer.get();
    }

    @Override
    public synchronized int read( byte[] bytes, int off, int len )
        throws IOException
    {
        len = Math.min( len, byteBuffer.remaining() );
        byteBuffer.get( bytes, off, len );
        return len;
    }
}
