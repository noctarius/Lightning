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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.directmemory.lightning.metadata.ClassDefinition;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class ClassUtil
{

    public static final ClassDefinition[] CLASS_DESCRIPTORS = new ClassDefinition[] {
        new JavaBuildInTypeClassDefinition( boolean.class, 1 ), new JavaBuildInTypeClassDefinition( Boolean.class, 2 ),
        new JavaBuildInTypeClassDefinition( byte.class, 3 ), new JavaBuildInTypeClassDefinition( Byte.class, 4 ),
        new JavaBuildInTypeClassDefinition( char.class, 5 ), new JavaBuildInTypeClassDefinition( Character.class, 6 ),
        new JavaBuildInTypeClassDefinition( double.class, 7 ), new JavaBuildInTypeClassDefinition( Double.class, 8 ),
        new JavaBuildInTypeClassDefinition( float.class, 9 ), new JavaBuildInTypeClassDefinition( Float.class, 10 ),
        new JavaBuildInTypeClassDefinition( int.class, 11 ), new JavaBuildInTypeClassDefinition( Integer.class, 12 ),
        new JavaBuildInTypeClassDefinition( long.class, 13 ), new JavaBuildInTypeClassDefinition( Long.class, 14 ),
        new JavaBuildInTypeClassDefinition( short.class, 15 ), new JavaBuildInTypeClassDefinition( Short.class, 16 ),
        new JavaBuildInTypeClassDefinition( String.class, 17 ), new JavaBuildInTypeClassDefinition( List.class, 18 ),
        new JavaBuildInTypeClassDefinition( Set.class, 19 ), new JavaBuildInTypeClassDefinition( Map.class, 20 ),
        new JavaBuildInTypeClassDefinition( BigInteger.class, 21 ),
        new JavaBuildInTypeClassDefinition( BigDecimal.class, 22 ) };

    private static final Map<Class<?>, Long> SERIAL_VERSION_UID_CACHE = new ConcurrentHashMap<Class<?>, Long>();

    private ClassUtil()
    {
    }

    public static boolean isReferenceCapable( Class<?> type )
    {
        return !type.isPrimitive() && Boolean.class != type && Byte.class != type && Short.class != type
            && Integer.class != type && Long.class != type && Float.class != type && Double.class != type;
    }

    public static Class<?> loadClass( String canonicalName )
        throws ClassNotFoundException
    {
        return loadClass( canonicalName, ClassUtil.class.getClassLoader() );
    }

    public static Class<?> loadClass( String canonicalName, ClassLoader classLoader )
        throws ClassNotFoundException
    {
        Class<?> type = null;
        try
        {
            type = classLoader.loadClass( canonicalName );
        }
        catch ( ClassNotFoundException e )
        {
            // Intentionally left blank
        }

        if ( type == null )
        {
            try
            {
                type = Class.forName( canonicalName );
            }
            catch ( ClassNotFoundException e )
            {
                // Intentionally left blank
            }
        }

        if ( type == null )
        {
            try
            {
                ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                type = tcl.loadClass( canonicalName );
            }
            catch ( ClassNotFoundException e )
            {
                // Intentionally left blank
            }
        }

        if ( type == null )
        {
            try
            {
                ClassLoader ccl = ClassUtil.class.getClassLoader();
                type = ccl.loadClass( canonicalName );
            }
            catch ( ClassNotFoundException e )
            {
                // Intentionally left blank
            }
        }

        if ( type != null )
        {
            return type;
        }

        throw new ClassNotFoundException( "Class " + canonicalName + " not found on classpath" );
    }

    public static long calculateSerialVersionUID( Class<?> clazz )
    {
        Long serialVersionUID = SERIAL_VERSION_UID_CACHE.get( clazz );
        if ( serialVersionUID != null )
        {
            return serialVersionUID;
        }

        if ( Serializable.class.isAssignableFrom( clazz ) )
        {
            serialVersionUID = ObjectStreamClass.lookup( clazz ).getSerialVersionUID();
            SERIAL_VERSION_UID_CACHE.put( clazz, serialVersionUID );
            return serialVersionUID;
        }

        serialVersionUID = getSerialVersionUIDFromField( clazz );
        if ( serialVersionUID != null )
        {
            SERIAL_VERSION_UID_CACHE.put( clazz, serialVersionUID );
            return serialVersionUID;
        }

        try
        {
            ClassReader reader = new ClassReader( Type.getInternalName( clazz ).replace( "/", "." ) );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream( baos );

            SerialVersionClassVisitor classVisitor = new SerialVersionClassVisitor();
            reader.accept( classVisitor, 0 );

            // Classname
            out.writeUTF( toJavaName( classVisitor.name ) );

            // Modifiers
            out.writeInt( clazz.getModifiers()
                & ( Modifier.PUBLIC | Modifier.FINAL | Modifier.INTERFACE | Modifier.ABSTRACT ) );

            // Interfaces
            Collections.sort( classVisitor.interfaces );
            for ( int i = 0; i < classVisitor.interfaces.size(); i++ )
            {
                out.writeUTF( toJavaName( classVisitor.interfaces.get( i ) ) );
            }

            // Fields
            Field[] fields = clazz.getDeclaredFields();
            Arrays.sort( fields, new Comparator<Field>()
            {

                @Override
                public int compare( Field o1, Field o2 )
                {
                    return o1.getName().compareTo( o2.getName() );
                }
            } );

            for ( Field field : fields )
            {
                int mods = field.getModifiers();
                if ( ( ( mods & Modifier.PRIVATE ) == 0 || ( mods & ( Modifier.STATIC | Modifier.TRANSIENT ) ) == 0 ) )
                {
                    out.writeUTF( field.getName() );
                    out.writeInt( mods );
                    out.writeUTF( Type.getDescriptor( field.getType() ) );
                }
            }

            // Static Initializer
            if ( classVisitor.staticInitializerFound )
            {
                out.writeUTF( "<clinit>" );
                out.writeInt( Modifier.STATIC );
                out.writeUTF( "()V" );
            }

            // Constructors
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Arrays.sort( constructors, new Comparator<Constructor<?>>()
            {

                @Override
                public int compare( Constructor<?> o1, Constructor<?> o2 )
                {
                    return Type.getConstructorDescriptor( o1 ).compareTo( Type.getConstructorDescriptor( o2 ) );
                }
            } );

            for ( int i = 0; i < constructors.length; i++ )
            {
                Constructor<?> constructor = constructors[i];
                int mods = constructor.getModifiers();
                if ( ( mods & Modifier.PRIVATE ) == 0 )
                {
                    out.writeUTF( "<init>" );
                    out.writeInt( mods );
                    out.writeUTF( toJavaName( Type.getConstructorDescriptor( constructor ) ) );
                }
            }

            // Methods
            Method[] methods = clazz.getDeclaredMethods();
            Arrays.sort( methods, new Comparator<Method>()
            {

                @Override
                public int compare( Method o1, Method o2 )
                {
                    return Type.getMethodDescriptor( o1 ).compareTo( Type.getMethodDescriptor( o2 ) );
                }
            } );

            for ( int i = 0; i < methods.length; i++ )
            {
                Method method = methods[i];
                int mods = method.getModifiers();
                if ( ( mods & Modifier.PRIVATE ) == 0 )
                {
                    out.writeUTF( "<init>" );
                    out.writeInt( mods );
                    out.writeUTF( toJavaName( Type.getMethodDescriptor( method ) ) );
                }
            }

            // Final calculation
            out.flush();
            MessageDigest digest = MessageDigest.getInstance( "SHA" );
            byte[] checksum = digest.digest( baos.toByteArray() );

            long hash = 0;
            for ( int i = Math.min( checksum.length, 8 ) - 1; i >= 0; i-- )
            {
                hash = ( hash << 8 ) | ( checksum[i] & 0xFF );
            }

            SERIAL_VERSION_UID_CACHE.put( clazz, hash );
            return hash;
        }
        catch ( IOException e )
        {
        }
        catch ( NoSuchAlgorithmException e )
        {
        }

        return -1L;
    }

    public static byte[] getClassBytes( Class<?> clazz )
    {
        try
        {
            ClassLoader classLoader = clazz.getClassLoader();
            if ( classLoader == null )
            {
                classLoader = Thread.currentThread().getContextClassLoader();
            }

            String internalName = Type.getInternalName( clazz );
            InputStream stream = classLoader.getResourceAsStream( internalName + ".class" );
            byte[] data = new byte[stream.available()];
            stream.read( data );
            stream.close();
            return data;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Class bytes could not be read", e );
        }
    }

    private static String toJavaName( String classname )
    {
        return classname.replace( "/", "." );
    }

    private static Long getSerialVersionUIDFromField( Class<?> clazz )
    {
        try
        {
            Field f = clazz.getDeclaredField( "serialVersionUID" );
            int mask = Modifier.STATIC | Modifier.FINAL;
            if ( ( f.getModifiers() & mask ) == mask )
            {
                f.setAccessible( true );
                return Long.valueOf( f.getLong( null ) );
            }
        }
        catch ( Exception ex )
        {
        }
        return null;
    }

    private static class JavaBuildInTypeClassDefinition
        implements ClassDefinition
    {

        private final long id;

        private final Class<?> type;

        private final String canonicalName;

        private final byte[] checksum = new byte[20];

        private final long serialVersionUID = -1L;

        JavaBuildInTypeClassDefinition( Class<?> type, long id )
        {
            this.id = id;
            this.type = type;
            this.canonicalName = type.getCanonicalName();
        }

        @Override
        public String getCanonicalName()
        {
            return canonicalName;
        }

        @Override
        public Class<?> getType()
        {
            return type;
        }

        @Override
        public byte[] getChecksum()
        {
            return checksum;
        }

        @Override
        public long getId()
        {
            return id;
        }

        @Override
        public long getSerialVersionUID()
        {
            return serialVersionUID;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( canonicalName == null ) ? 0 : canonicalName.hashCode() );
            result = prime * result + Arrays.hashCode( checksum );
            result = prime * result + (int) ( id ^ ( id >>> 32 ) );
            result = prime * result + (int) ( serialVersionUID ^ ( serialVersionUID >>> 32 ) );
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
            JavaBuildInTypeClassDefinition other = (JavaBuildInTypeClassDefinition) obj;
            if ( canonicalName == null )
            {
                if ( other.canonicalName != null )
                    return false;
            }
            else if ( !canonicalName.equals( other.canonicalName ) )
                return false;
            if ( !Arrays.equals( checksum, other.checksum ) )
                return false;
            if ( id != other.id )
                return false;
            if ( serialVersionUID != other.serialVersionUID )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "JavaBuildInTypeClassDefinition [id=" + id + ", type=" + type + ", canonicalName=" + canonicalName
                + ", checksum=" + Arrays.toString( checksum ) + ", serialVersionUID=" + serialVersionUID + "]";
        }
    }

    private static class SerialVersionClassVisitor
        extends ClassVisitor
    {

        public SerialVersionClassVisitor()
        {
            super( Opcodes.ASM4 );
        }

        private List<String> interfaces = new ArrayList<String>();

        private boolean staticInitializerFound = false;

        private String name;

        @Override
        public void visit( int version, int access, String name, String signature, String superName, String[] interfaces )
        {
            this.name = name;
            this.interfaces = Arrays.asList( interfaces );
        }

        @Override
        public AnnotationVisitor visitAnnotation( String desc, boolean visible )
        {
            return null;
        }

        @Override
        public void visitAttribute( Attribute attr )
        {
        }

        @Override
        public void visitEnd()
        {
        }

        @Override
        public FieldVisitor visitField( int access, String name, String desc, String signature, Object value )
        {
            return null;
        }

        @Override
        public void visitInnerClass( String name, String outerName, String innerName, int access )
        {
        }

        @Override
        public MethodVisitor visitMethod( int access, String name, String desc, String signature, String[] exceptions )
        {
            if ( "<clinit>".equals( name ) && ( access & Opcodes.ACC_STATIC ) != 0 )
            {
                staticInitializerFound = true;
            }
            return null;
        }

        @Override
        public void visitOuterClass( String owner, String name, String desc )
        {
        }

        @Override
        public void visitSource( String source, String debug )
        {
        }
    }
}
