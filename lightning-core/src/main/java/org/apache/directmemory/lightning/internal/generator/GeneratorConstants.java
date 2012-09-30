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
package org.apache.directmemory.lightning.internal.generator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.directmemory.lightning.Marshaller;
import org.apache.directmemory.lightning.MarshallerContext;
import org.apache.directmemory.lightning.MarshallerStrategy;
import org.apache.directmemory.lightning.SerializationContext;
import org.apache.directmemory.lightning.instantiator.ObjectInstantiatorFactory;
import org.apache.directmemory.lightning.internal.CheatPropertyDescriptor;
import org.apache.directmemory.lightning.internal.ClassDescriptorAwareSerializer;
import org.apache.directmemory.lightning.metadata.ArrayPropertyAccessor;
import org.apache.directmemory.lightning.metadata.PropertyAccessor;
import org.apache.directmemory.lightning.metadata.PropertyDescriptor;
import org.apache.directmemory.lightning.metadata.ValuePropertyAccessor;
import org.objectweb.asm.Type;

public interface GeneratorConstants
{

    static String MARSHALLER_MARSHALL_SIGNATURE =
        Type.getMethodDescriptor( Type.VOID_TYPE,
                                  new Type[] { Type.getType( Object.class ), Type.getType( PropertyDescriptor.class ),
                                      Type.getType( DataOutput.class ), Type.getType( SerializationContext.class ) } );

    static String MARSHALLER_BASE_UNMARSHALL_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] { Type.getType( PropertyDescriptor.class ),
            Type.getType( DataInput.class ), Type.getType( SerializationContext.class ) } );

    static String MARSHALLER_UNMARSHALL_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( Object.class ),
                                  new Type[] { Type.getType( Object.class ), Type.getType( PropertyDescriptor.class ),
                                      Type.getType( DataInput.class ), Type.getType( SerializationContext.class ) } );

    static String MARSHALLER_FIND_MARSHALLER_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( Marshaller.class ),
                                  new Type[] { Type.getType( PropertyDescriptor.class ) } );

    static String MARSHALLER_GET_PROPERTY_ACCESSOR_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( PropertyAccessor.class ), new Type[] { Type.getType( String.class ) } );

    static String MARSHALLER_IS_ALREADY_MARSHALLED_SIGNATURE =
        Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                  new Type[] { Type.getType( Object.class ), Type.getType( Class.class ),
                                      Type.getType( DataOutput.class ), Type.getType( SerializationContext.class ) } );

    static String MARSHALLER_CONSTRUCTOR_SIGNATURE =
        Type.getMethodDescriptor( Type.VOID_TYPE,
                                  new Type[] { Type.getType( Class.class ), Type.getType( Map.class ),
                                      Type.getType( ClassDescriptorAwareSerializer.class ),
                                      Type.getType( ObjectInstantiatorFactory.class ), Type.getType( List.class ),
                                      Type.getType( MarshallerStrategy.class ) } );

    static String MARSHALLER_SUPER_CONSTRUCTOR_SIGNATURE =
        Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( Class.class ), Type.getType( Map.class ),
            Type.getType( ClassDescriptorAwareSerializer.class ), Type.getType( ObjectInstantiatorFactory.class ) } );

    static String PROPERTY_DESCRIPTOR_GET_MARSHALLER_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( Marshaller.class ), new Type[0] );

    static String PROPERTY_DESCRIPTOR_GET_PROPERTYACCESSOR_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( PropertyAccessor.class ), new Type[0] );

    static String OBJECT_GET_CLASS_SIGNATURE = Type.getMethodDescriptor( Type.getType( Class.class ), new Type[0] );

    static String CLASS_GET_COMPONENT_TYPE = Type.getMethodDescriptor( Type.getType( Class.class ), new Type[0] );

    static String ARRAY_LENGTH_SIGNATURE = Type.getMethodDescriptor( Type.INT_TYPE, new Type[0] );

    static String MARSHALLERSTRATEGY_GET_MARSHALLER_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( Marshaller.class ),
                                  new Type[] { Type.getType( java.lang.reflect.Type.class ),
                                      Type.getType( MarshallerContext.class ) } );

    static String CHEATINGPROPERTYDESCRIPTOR_CONSTRUCTOR =
        Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( String.class ),
            Type.getType( Class.class ), Type.getType( Marshaller.class ) } );

    static String SUPER_CLASS_INTERNAL_TYPE = Type.getType( AbstractGeneratedMarshaller.class ).getInternalName();

    static String MARSHALLER_CLASS_INTERNAL_TYPE = Type.getType( Marshaller.class ).getInternalName();

    static String IOEXCEPTION_CLASS_INTERNAL_TYPE = Type.getType( IOException.class ).getInternalName();

    static String LIST_CLASS_INTERNAL_TYPE = Type.getType( List.class ).getInternalName();

    static String PROPERTYACCESSOR_CLASS_INTERNAL_TYPE = Type.getType( PropertyAccessor.class ).getInternalName();

    static String VALUEPROPERTYACCESSOR_CLASS_INTERNAL_TYPE =
        Type.getType( ValuePropertyAccessor.class ).getInternalName();

    static String ARRAYPROPERTYACCESSOR_CLASS_INTERNAL_TYPE =
        Type.getType( ArrayPropertyAccessor.class ).getInternalName();

    static String PROPERTYDESCRIPTOR_CLASS_INTERNAL_TYPE = Type.getType( PropertyDescriptor.class ).getInternalName();

    static String CHEATINGPROPERTYDESCRIPTOR_CLASS_INTERNAL_TYPE =
        Type.getType( CheatPropertyDescriptor.class ).getInternalName();

    static String CLASS_CLASS_INTERNAL_TYPE = Type.getType( Class.class ).getInternalName();

    static String DATAOUTPUT_CLASS_INTERNAL_TYPE = Type.getType( DataOutput.class ).getInternalName();

    static String DATAINPUT_CLASS_INTERNAL_TYPE = Type.getType( DataInput.class ).getInternalName();

    static String MARSHALLERSTRATEGY_CLASS_INTERNAL_TYPE = Type.getType( MarshallerStrategy.class ).getInternalName();

    static String MARSHALLER_CLASS_DESCRIPTOR = Type.getType( Marshaller.class ).getDescriptor();

    static String PROPERTYDESCRIPTOR_CLASS_DESCRIPTOR = Type.getType( PropertyDescriptor.class ).getDescriptor();

    static String PROPERTYACCESSOR_CLASS_DESCRIPTOR = Type.getType( PropertyAccessor.class ).getDescriptor();

    static String CHEATINGPROPERTYDESCRIPTOR_CLASS_DESCRIPTOR =
        Type.getType( CheatPropertyDescriptor.class ).getDescriptor();

    static String[] MARSHALLER_EXCEPTIONS = { IOEXCEPTION_CLASS_INTERNAL_TYPE };

    static AtomicLong GENEREATED_CLASS_ID = new AtomicLong();

    static String PROPERTY_DESCRIPTOR_FIELD_NAME = "PROPERTY_DESCRIPTORS";

    static String PROPERTY_ACCESSOR_READ_BOOLEAN_SIGNATURE =
        Type.getMethodDescriptor( Type.BOOLEAN_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_BYTE_SIGNATURE =
        Type.getMethodDescriptor( Type.BYTE_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_CHAR_SIGNATURE =
        Type.getMethodDescriptor( Type.CHAR_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_SHORT_SIGNATURE =
        Type.getMethodDescriptor( Type.SHORT_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_INT_SIGNATURE =
        Type.getMethodDescriptor( Type.INT_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_LONG_SIGNATURE =
        Type.getMethodDescriptor( Type.LONG_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_FLOAT_SIGNATURE =
        Type.getMethodDescriptor( Type.FLOAT_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_DOUBLE_SIGNATURE =
        Type.getMethodDescriptor( Type.DOUBLE_TYPE, new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_READ_OBJECT_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] { Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_WRITE_BOOLEAN_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.BOOLEAN_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_BYTE_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                                     new Type[] {
                                                                                         Type.getType( Object.class ),
                                                                                         Type.BYTE_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_CHAR_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                                     new Type[] {
                                                                                         Type.getType( Object.class ),
                                                                                         Type.CHAR_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_SHORT_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                                      new Type[] {
                                                                                          Type.getType( Object.class ),
                                                                                          Type.SHORT_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_INT_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                                    new Type[] {
                                                                                        Type.getType( Object.class ),
                                                                                        Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_LONG_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                                     new Type[] {
                                                                                         Type.getType( Object.class ),
                                                                                         Type.LONG_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_FLOAT_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                                      new Type[] {
                                                                                          Type.getType( Object.class ),
                                                                                          Type.FLOAT_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_DOUBLE_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.DOUBLE_TYPE } );

    static String PROPERTY_ACCESSOR_WRITE_OBJECT_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.getType( Object.class ) } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_BOOLEAN_SIGNATURE =
        Type.getMethodDescriptor( Type.BOOLEAN_TYPE, new Type[] { Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_BYTE_SIGNATURE = Type.getMethodDescriptor( Type.BYTE_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_CHAR_SIGNATURE = Type.getMethodDescriptor( Type.CHAR_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_SHORT_SIGNATURE = Type.getMethodDescriptor( Type.SHORT_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_INT_SIGNATURE = Type.getMethodDescriptor( Type.INT_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_LONG_SIGNATURE = Type.getMethodDescriptor( Type.LONG_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_FLOAT_SIGNATURE = Type.getMethodDescriptor( Type.FLOAT_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_DOUBLE_SIGNATURE =
        Type.getMethodDescriptor( Type.DOUBLE_TYPE, new Type[] { Type.getType( Object.class ), Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_READ_OBJECT_SIGNATURE =
        Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] { Type.getType( Object.class ),
            Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_BOOLEAN_SIGNATURE =
        Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( Object.class ), Type.INT_TYPE,
            Type.BOOLEAN_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_BYTE_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE, Type.BYTE_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_CHAR_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE, Type.CHAR_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_SHORT_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE, Type.SHORT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_INT_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE, Type.INT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_LONG_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE, Type.LONG_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_FLOAT_SIGNATURE = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {
        Type.getType( Object.class ), Type.INT_TYPE, Type.FLOAT_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_DOUBLE_SIGNATURE =
        Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( Object.class ), Type.INT_TYPE,
            Type.DOUBLE_TYPE } );

    static String PROPERTY_ACCESSOR_ARRAY_WRITE_OBJECT_SIGNATURE =
        Type.getMethodDescriptor( Type.VOID_TYPE,
                                  new Type[] { Type.getType( Object.class ), Type.INT_TYPE, Type.getType( Object.class ) } );

    static String BOOLEAN_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Boolean.class ),
                                                                         new Type[] { Type.BOOLEAN_TYPE } );

    static String BYTE_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Byte.class ),
                                                                      new Type[] { Type.BYTE_TYPE } );

    static String CHAR_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Character.class ),
                                                                      new Type[] { Type.CHAR_TYPE } );

    static String SHORT_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Short.class ),
                                                                       new Type[] { Type.SHORT_TYPE } );

    static String INT_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Integer.class ),
                                                                     new Type[] { Type.INT_TYPE } );

    static String LONG_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Long.class ),
                                                                      new Type[] { Type.LONG_TYPE } );

    static String FLOAT_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Float.class ),
                                                                       new Type[] { Type.FLOAT_TYPE } );

    static String DOUBLE_VALUE_OF_SIGNATURE = Type.getMethodDescriptor( Type.getType( Double.class ),
                                                                        new Type[] { Type.DOUBLE_TYPE } );

    static String ARRAY_TYPE_BOOLEAN = Type.getInternalName( boolean[].class );

    static String ARRAY_TYPE_BYTE = Type.getInternalName( byte[].class );

    static String ARRAY_TYPE_CHAR = Type.getInternalName( char[].class );

    static String ARRAY_TYPE_SHORT = Type.getInternalName( short[].class );

    static String ARRAY_TYPE_INT = Type.getInternalName( int[].class );

    static String ARRAY_TYPE_LONG = Type.getInternalName( long[].class );

    static String ARRAY_TYPE_FLOAT = Type.getInternalName( float[].class );

    static String ARRAY_TYPE_DOUBLE = Type.getInternalName( double[].class );

    static String ARRAY_TYPE_OBJECT = Type.getInternalName( Object[].class );
}
