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

public final class StringUtil
{

    private StringUtil()
    {
    }

    public static String toLowerCamelCase( String value )
    {
        if ( value == null )
            return null;

        if ( value.length() == 0 )
            return value;

        return toCamelCase( value );
    }

    public static String toUpperCamelCase( String value )
    {
        String lowerCamelCase = toLowerCamelCase( value );
        return String.valueOf( Character.toUpperCase( lowerCamelCase.toCharArray()[0] ) )
            + lowerCamelCase.substring( 1 );
    }

    private static String toCamelCase( String value )
    {
        StringBuilder camelCase = new StringBuilder();
        char[] characters = value.toCharArray();

        for ( int i = 0; i < characters.length; i++ )
        {
            i = ignoreWhitespace( characters, i );
            if ( i == -1 )
                break;

            if ( camelCase.length() == 0 )
                camelCase.append( Character.toLowerCase( characters[i] ) );
            else
                camelCase.append( Character.toUpperCase( characters[i] ) );

            int nextWhitespace = nextWhitespace( characters, i );
            if ( nextWhitespace == -1 )
                nextWhitespace = characters.length;

            camelCase.append( value.substring( i + 1, nextWhitespace ) );
            i = nextWhitespace;
        }

        return camelCase.toString();
    }

    private static int ignoreWhitespace( char[] characters, int offset )
    {
        for ( int i = offset; i < characters.length; i++ )
        {
            char c = characters[i];
            if ( !Character.isWhitespace( c ) && '-' != c && '_' != c )
                return i;
        }
        return -1;
    }

    private static int nextWhitespace( char[] characters, int offset )
    {
        for ( int i = offset + 1; i < characters.length; i++ )
        {
            char c = characters[i];
            if ( Character.isWhitespace( c ) || '-' == c || '_' == c )
                return i;
        }
        return -1;
    }

}
