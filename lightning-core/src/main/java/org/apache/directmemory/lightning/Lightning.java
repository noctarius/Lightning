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

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.directmemory.lightning.ClassComparisonStrategy;
import org.apache.directmemory.lightning.SerializationStrategy;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.base.DefaultValueNullableEvaluator;
import org.apache.directmemory.lightning.configuration.SerializerDefinition;
import org.apache.directmemory.lightning.internal.InternalSerializerCreator;
import org.apache.directmemory.lightning.logging.Logger;
import org.apache.directmemory.lightning.logging.LoggerAdapter;
import org.apache.directmemory.lightning.metadata.ValueNullableEvaluator;

public final class Lightning
{

    private Lightning()
    {
    }

    public static final Builder newBuilder()
    {
        return new Builder();
    }

    public static final Serializer createSerializer( SerializerDefinition... serializerDefinitions )
    {
        return createSerializer( Arrays.asList( serializerDefinitions ) );
    }

    public static final Serializer createSerializer( Iterable<? extends SerializerDefinition> serializerDefinitions )
    {
        return new Builder().serializerDefinitions( serializerDefinitions ).build();
    }

    public static class Builder
    {

        private Set<SerializerDefinition> serializerDefinitions = new HashSet<SerializerDefinition>();

        private SerializationStrategy serializationStrategy = SerializationStrategy.SpeedOptimized;

        private Class<? extends Annotation> attributeAnnotation = null;

        private ClassComparisonStrategy classComparisonStrategy = ClassComparisonStrategy.LightningChecksum;

        private ValueNullableEvaluator valueNullableEvaluator = new DefaultValueNullableEvaluator();

        private File debugCacheDirectory = null;

        private Logger logger = new LoggerAdapter();

        private Builder()
        {
        }

        public Builder describesAttributs( Class<? extends Annotation> attributeAnnotation )
        {
            this.attributeAnnotation = attributeAnnotation;
            return this;
        }

        public Builder debugCacheDirectory( File debugCacheDirectory )
        {
            this.debugCacheDirectory = debugCacheDirectory;
            return this;
        }

        public Builder serializationStrategy( SerializationStrategy serializationStrategy )
        {
            this.serializationStrategy = serializationStrategy;
            return this;
        }

        public Builder classComparisonStrategy( ClassComparisonStrategy classComparisonStrategy )
        {
            this.classComparisonStrategy = classComparisonStrategy;
            return this;
        }

        public Builder serializerDefinitions( SerializerDefinition... serializerDefinitions )
        {
            return serializerDefinitions( Arrays.asList( serializerDefinitions ) );
        }

        public Builder serializerDefinitions( Iterable<? extends SerializerDefinition> serializerDefinitions )
        {
            for ( SerializerDefinition serializerDefinition : serializerDefinitions )
            {
                this.serializerDefinitions.add( serializerDefinition );
            }
            return this;
        }

        public Builder setValueNullableEvaluator( ValueNullableEvaluator valueNullableEvaluator )
        {
            this.valueNullableEvaluator = valueNullableEvaluator;
            return this;
        }

        public Builder logger( Logger logger )
        {
            this.logger = logger;
            return this;
        }

        public Serializer build()
        {
            return new InternalSerializerCreator().setLogger( logger ).setSerializationStrategy( serializationStrategy ).setClassComparisonStrategy( classComparisonStrategy ).setAttributeAnnotation( attributeAnnotation ).setDebugCacheDirectory( debugCacheDirectory ).setValueNullableEvaluator( valueNullableEvaluator ).addSerializerDefinitions( serializerDefinitions ).build();
        }
    }

}
