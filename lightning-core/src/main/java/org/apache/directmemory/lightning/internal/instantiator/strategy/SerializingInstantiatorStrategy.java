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
import org.apache.directmemory.lightning.internal.instantiator.basic.ObjectStreamClassInstantiator;
import org.apache.directmemory.lightning.internal.instantiator.gcj.GCJSerializationInstantiator;
import org.apache.directmemory.lightning.internal.instantiator.perc.PercSerializationInstantiator;
import org.apache.directmemory.lightning.internal.instantiator.sun.Sun13SerializationInstantiator;
import org.apache.directmemory.lightning.internal.util.InternalUtil;

/**
 * Guess the best serializing instantiator for a given class. The returned instantiator will instantiate classes like
 * the genuine java serialization framework (the constructor of the first not serializable class will be called).
 * Currently, the selection doesn't depend on the class. It relies on the
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
public class SerializingInstantiatorStrategy
    extends BaseInstantiatorStrategy
{

    /**
     * Return an {@link ObjectInstantiator} allowing to create instance following the java serialization framework
     * specifications.
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
                return new Sun13SerializationInstantiator( type );
            }
            else if ( InternalUtil.isUnsafeAvailable() )
            {
                return InternalUtil.buildSunUnsafeInstantiator( type );
            }
        }
        else if ( JVM_NAME.startsWith( GNU ) )
        {
            return new GCJSerializationInstantiator( type );
        }
        else if ( JVM_NAME.startsWith( PERC ) )
        {
            return new PercSerializationInstantiator( type );
        }

        return new ObjectStreamClassInstantiator( type );
    }
}
