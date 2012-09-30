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

public enum SerializationStrategy
{

    /**
     * This strategy does not force same instances to become same instances on deserialization since only values are
     * written to the stream.<br>
     * To be clear, deserialized instances of same objects are non identity-equal!
     */
    SpeedOptimized,

    /**
     * This strategy forces same instances to become same instances on deserialization. This needs to collect instances
     * by hashCode on both sides while serialization and deserialization, which in case needs time.<br>
     * To be clear, deserialized instances of same objects are identity-equal!
     */
    SizeOptimized

}
