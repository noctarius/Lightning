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
package org.apache.directmemory.lightning.internal.util;

/**
 * Crc64 checksum computation. This classes basic content was copied from original position:
 * http://intact.googlecode.com/ It was changed to use byte data instead of strings
 * 
 * @author The European Bioinformatics Institute, and others.
 * @author Uniparc
 * @version $Id$
 */
public final class Crc64Util
{

    private static long _crc64Array[] = new long[256];

    /**
     * Initialization of _crc64Array.
     */
    static
    {

        for ( int i = 0; i <= 255; ++i )
        {
            long k = i;
            for ( int j = 0; j < 8; ++j )
            {
                if ( ( k & 1 ) != 0 )
                {
                    k = ( k >>> 1 ) ^ 0xd800000000000000l;
                }
                else
                {
                    k = k >>> 1;
                }
            }
            _crc64Array[i] = k;
        }
    }

    private Crc64Util()
    {
    }

    /**
     * Returns a hex string representation of the checksum
     * 
     * @param checksum
     * @return
     */
    public static String toString( long checksum )
    {
        String crc64String = Long.toHexString( checksum ).toUpperCase();
        StringBuffer crc64 = new StringBuffer( "0000000000000000" );
        crc64.replace( crc64.length() - crc64String.length(), crc64.length(), crc64String );

        return crc64.toString();
    }

    /**
     * Calculated the checksum of the given data array as a long value.
     * 
     * @param data the data to checksum
     * @return the calculated checksum
     */
    public static long checksum( byte[] data )
    {
        long crc64Number = 0;
        for ( int i = 0; i < data.length; ++i )
        {
            int symbol = data[i];
            long a = ( crc64Number >>> 8 );
            long b = ( crc64Number ^ symbol ) & 0xff;
            crc64Number = a ^ _crc64Array[(int) b];
        }

        return crc64Number;
    }
}
