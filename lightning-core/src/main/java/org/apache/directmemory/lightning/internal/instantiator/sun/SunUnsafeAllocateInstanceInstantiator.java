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
package org.apache.directmemory.lightning.internal.instantiator.sun;

import org.apache.directmemory.lightning.instantiator.ObjectInstantiator;
import org.apache.directmemory.lightning.internal.util.UnsafeUtil;

@SuppressWarnings( "restriction" )
public class SunUnsafeAllocateInstanceInstantiator
    implements ObjectInstantiator
{

    private static final sun.misc.Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    protected final Class<?> type;

    public SunUnsafeAllocateInstanceInstantiator( Class<?> type )
    {
        this.type = type;
    }

    @Override
    public Object newInstance()
    {
        try
        {
            if ( UNSAFE != null )
                return UNSAFE.allocateInstance( type );
        }
        catch ( Exception e )
        {
            // ignore and return null
        }

        return null;
    }
}
