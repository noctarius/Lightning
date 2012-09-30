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
package org.apache.directmemory.lightning.internal.instantiator.strategy;

import org.apache.directmemory.lightning.instantiator.ObjectInstantiator;
import org.apache.directmemory.lightning.internal.instantiator.gcj.GCJInstantiator;
import org.apache.directmemory.lightning.internal.instantiator.perc.PercInstantiator;
import org.apache.directmemory.lightning.internal.instantiator.sun.Sun13Instantiator;
import org.apache.directmemory.lightning.internal.instantiator.sun.SunReflectionFactoryInstantiator;
import org.apache.directmemory.lightning.internal.util.InternalUtil;

/**
 * Guess the best instantiator for a given class. The instantiator will instantiate the class without calling any
 * constructor. Currently, the selection doesn't depend on the class. It relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 * 
 * @author Henri Tremblay
 * @see ObjectInstantiator
 */
public class StdInstantiatorStrategy
    extends BaseInstantiatorStrategy
{

    /**
     * Return an {@link ObjectInstantiator} allowing to create instance without any constructor being called.
     * 
     * @param type Class to instantiate
     * @return The ObjectInstantiator for the class
     */
    @Override
    public ObjectInstantiator newInstantiatorOf( Class<?> type )
    {

        if ( JVM_NAME.startsWith( SUN ) )
        {
            if ( VM_VERSION.startsWith( "1.3" ) )
            {
                return new Sun13Instantiator( type );
            }
            else if ( InternalUtil.isUnsafeAvailable() )
            {
                return InternalUtil.buildSunUnsafeInstantiator( type );
            }
        }
        else if ( JVM_NAME.startsWith( ORACLE_JROCKIT ) )
        {
            if ( !VENDOR_VERSION.startsWith( "R" ) )
            {
                // Beginning with R25.1 sun.misc.Unsafe should work.
                if ( InternalUtil.isUnsafeAvailable() )
                {
                    return InternalUtil.buildSunUnsafeInstantiator( type );
                }
            }
        }
        else if ( JVM_NAME.startsWith( GNU ) )
        {
            return new GCJInstantiator( type );
        }
        else if ( JVM_NAME.startsWith( PERC ) )
        {
            return new PercInstantiator( type );
        }

        // Fallback instantiator, should work with:
        // - Java Hotspot version 1.4 and higher
        // - JRockit 1.4-R26 and higher
        // - IBM and Hitachi JVMs
        // ... might works for others so we just give it a try
        return new SunReflectionFactoryInstantiator( type );
    }
}
