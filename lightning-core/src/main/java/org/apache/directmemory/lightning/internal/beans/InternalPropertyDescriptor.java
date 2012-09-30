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
package org.apache.directmemory.lightning.internal.beans;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.internal.util.BeanUtil;
import org.apache.directmemory.lightning.internal.util.StringUtil;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;

class InternalPropertyDescriptor
    implements PropertyDescriptor
{

    private final String name;

    private final String propertyName;

    private final String internalSignature;

    private final String declaringCanonicalClassname;

    private final Class<?> definedClass;

    private final Class<?> declaringClass;

    private final PropertyAccessor propertyAccessor;

    private final Annotation[] annotations;

    private final Marshaller marshaller;

    InternalPropertyDescriptor( String propertyName, Marshaller marshaller, Annotation[] annotations,
                                PropertyAccessor propertyAccessor )
    {
        this.name = StringUtil.toUpperCamelCase( propertyName );
        this.propertyName = propertyName;
        this.propertyAccessor = propertyAccessor;
        this.marshaller = marshaller;
        this.declaringCanonicalClassname = propertyAccessor.getType().getCanonicalName();
        this.internalSignature = BeanUtil.buildInternalSignature( propertyName, propertyAccessor );
        this.annotations = Arrays.copyOf( annotations, annotations.length );
        this.definedClass = propertyAccessor.getDefinedClass();
        this.declaringClass = propertyAccessor.getDeclaringClass();
    }

    @Override
    public Class<?> getDefinedClass()
    {
        return definedClass;
    }

    @Override
    public Class<?> getDeclaringClass()
    {
        return declaringClass;
    }

    @Override
    public PropertyAccessor getPropertyAccessor()
    {
        return propertyAccessor;
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return Arrays.copyOf( annotations, annotations.length );
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getPropertyName()
    {
        return propertyName;
    }

    @Override
    public Class<?> getType()
    {
        return propertyAccessor.getType();
    }

    @Override
    public String getInternalSignature()
    {
        return internalSignature;
    }

    @Override
    public Marshaller getMarshaller()
    {
        return marshaller;
    }

    @Override
    public int compareTo( PropertyDescriptor o )
    {
        return propertyName.compareTo( o.getPropertyName() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( annotations );
        result =
            prime * result + ( ( declaringCanonicalClassname == null ) ? 0 : declaringCanonicalClassname.hashCode() );
        result = prime * result + ( ( declaringClass == null ) ? 0 : declaringClass.hashCode() );
        result = prime * result + ( ( definedClass == null ) ? 0 : definedClass.hashCode() );
        result = prime * result + ( ( internalSignature == null ) ? 0 : internalSignature.hashCode() );
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( propertyName == null ) ? 0 : propertyName.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        InternalPropertyDescriptor other = (InternalPropertyDescriptor) obj;
        if ( !Arrays.equals( annotations, other.annotations ) )
            return false;
        if ( declaringCanonicalClassname == null )
        {
            if ( other.declaringCanonicalClassname != null )
                return false;
        }
        else if ( !declaringCanonicalClassname.equals( other.declaringCanonicalClassname ) )
            return false;
        if ( declaringClass == null )
        {
            if ( other.declaringClass != null )
                return false;
        }
        else if ( !declaringClass.equals( other.declaringClass ) )
            return false;
        if ( definedClass == null )
        {
            if ( other.definedClass != null )
                return false;
        }
        else if ( !definedClass.equals( other.definedClass ) )
            return false;
        if ( internalSignature == null )
        {
            if ( other.internalSignature != null )
                return false;
        }
        else if ( !internalSignature.equals( other.internalSignature ) )
            return false;
        if ( name == null )
        {
            if ( other.name != null )
                return false;
        }
        else if ( !name.equals( other.name ) )
            return false;
        if ( propertyName == null )
        {
            if ( other.propertyName != null )
                return false;
        }
        else if ( !propertyName.equals( other.propertyName ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "InternalPropertyDescriptor [name=" + name + ", propertyName=" + propertyName + ", internalSignature="
            + internalSignature + ", declaringCanonicalClassname=" + declaringCanonicalClassname + ", definedClass="
            + definedClass + ", declaringClass=" + declaringClass + ", propertyAccessor=" + propertyAccessor
            + ", annotations=" + Arrays.toString( annotations ) + ", marshaller=" + marshaller + "]";
    }
}
