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
package org.apache.directmemory.lightning.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.apache.directmemory.lightning.ClassComparisonStrategy;
import org.apache.directmemory.lightning.Lightning;
import org.apache.directmemory.lightning.SerializationStrategy;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.configuration.SerializerDefinition;
import org.apache.directmemory.lightning.logging.Logger;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class LightningFactoryBean
    implements FactoryBean, InitializingBean
{

    private Serializer singletonSerializer;

    private boolean prototype = false;

    private List<SerializerDefinition> serializerDefinitions = new ArrayList<SerializerDefinition>();

    private ClassComparisonStrategy classComparisonStrategy = ClassComparisonStrategy.LightningChecksum;

    private Class<? extends Annotation> attributesAnnotation = Attribute.class;

    private SerializationStrategy serializationStrategy = SerializationStrategy.SpeedOptimized;

    private Logger logger = new SpringLoggingAdapter();

    private File debugCacheDirectory = null;

    @Override
    public Object getObject()
        throws Exception
    {
        if ( isSingleton() )
        {
            return singletonSerializer;
        }

        return Lightning.newBuilder().classComparisonStrategy( classComparisonStrategy ).debugCacheDirectory( debugCacheDirectory ).describesAttributs( attributesAnnotation ).logger( logger ).serializationStrategy( serializationStrategy ).serializerDefinitions( serializerDefinitions ).build();
    }

    @Override
    public Class<?> getObjectType()
    {
        return Serializer.class;
    }

    @Override
    public boolean isSingleton()
    {
        return !prototype;
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        if ( isSingleton() )
        {
            synchronized ( this )
            {
                singletonSerializer =
                    Lightning.newBuilder().classComparisonStrategy( classComparisonStrategy ).debugCacheDirectory( debugCacheDirectory ).describesAttributs( attributesAnnotation ).logger( logger ).serializationStrategy( serializationStrategy ).serializerDefinitions( serializerDefinitions ).build();
            }
        }
    }

    public void setPrototype( boolean prototype )
    {
        this.prototype = prototype;
    }

    public boolean getPrototype()
    {
        return prototype;
    }

    public List<SerializerDefinition> getSerializerDefinitions()
    {
        return serializerDefinitions;
    }

    public void setSerializerDefinitions( List<SerializerDefinition> serializerDefinitions )
    {
        this.serializerDefinitions = serializerDefinitions;
    }

    public ClassComparisonStrategy getClassComparisonStrategy()
    {
        return classComparisonStrategy;
    }

    public void setClassComparisonStrategy( ClassComparisonStrategy classComparisonStrategy )
    {
        this.classComparisonStrategy = classComparisonStrategy;
    }

    public File getDebugCacheDirectory()
    {
        return debugCacheDirectory;
    }

    public void setDebugCacheDirectory( File debugCacheDirectory )
    {
        this.debugCacheDirectory = debugCacheDirectory;
    }

    public Class<? extends Annotation> getAttributesAnnotation()
    {
        return attributesAnnotation;
    }

    public void setAttributesAnnotation( Class<? extends Annotation> attributesAnnotation )
    {
        this.attributesAnnotation = attributesAnnotation;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public void setLogger( Logger logger )
    {
        this.logger = logger;
    }

    public SerializationStrategy getSerializationStrategy()
    {
        return serializationStrategy;
    }

    public void setSerializationStrategy( SerializationStrategy serializationStrategy )
    {
        this.serializationStrategy = serializationStrategy;
    }
}
