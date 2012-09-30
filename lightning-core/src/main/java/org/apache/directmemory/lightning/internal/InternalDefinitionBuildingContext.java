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
package org.apache.directmemory.lightning.internal;

import org.apache.directmemory.lightning.MarshallerStrategy;
import org.apache.directmemory.lightning.generator.DefinitionBuildingContext;
import org.apache.directmemory.lightning.generator.PropertyDescriptorFactory;

public class InternalDefinitionBuildingContext
    implements DefinitionBuildingContext
{

    private final PropertyDescriptorFactory propertyDescriptorFactory;

    private final MarshallerStrategy marshallerStrategy;

    public InternalDefinitionBuildingContext( MarshallerStrategy marshallerStrategy,
                                              PropertyDescriptorFactory propertyDescriptorFactory )
    {
        this.marshallerStrategy = marshallerStrategy;
        this.propertyDescriptorFactory = propertyDescriptorFactory;
    }

    @Override
    public PropertyDescriptorFactory getPropertyDescriptorFactory()
    {
        return propertyDescriptorFactory;
    }

    @Override
    public MarshallerStrategy getMarshallerStrategy()
    {
        return marshallerStrategy;
    }
}
