/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lightning.internal.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.lightning.Marshaller;
import com.github.lightning.SerializationStrategy;
import com.github.lightning.exceptions.SerializerMarshallerGeneratorException;
import com.github.lightning.instantiator.ObjectInstantiatorFactory;
import com.github.lightning.internal.ClassDescriptorAwareSerializer;
import com.github.lightning.internal.util.ClassUtil;
import com.github.lightning.metadata.PropertyDescriptor;

public class BytecodeMarshallerGenerator implements Opcodes, GeneratorConstants, MarshallerGenerator {

	private final GeneratorClassLoader classloader = CreateClassLoader.createClassLoader(getClass().getClassLoader());

	public Marshaller generateMarshaller(Class<?> type, List<PropertyDescriptor> propertyDescriptors,
			Map<Class<?>, Marshaller> marshallers, ClassDescriptorAwareSerializer serializer,
			SerializationStrategy serializationStrategy, ObjectInstantiatorFactory objectInstantiatorFactory,
			File debugCacheDirectory) {

		try {
			ClassWriter cw = new ClassWriter(0);

			// Copy properties and sort them by name
			List<PropertyDescriptor> propertyDescriptorsCopy = new ArrayList<PropertyDescriptor>(propertyDescriptors);
			Collections.sort(propertyDescriptorsCopy);

			// Build className e.g. "SomeTypeMarshaller$$X$$Lightning"
			String className = new StringBuilder(type.getSimpleName()).append("Marshaller")
					.append(GENEREATED_CLASS_ID.getAndIncrement()).append("Lightning").toString();

			// Build class
			cw.visit(V1_6, ACC_PUBLIC & ACC_SUPER, className, null, SUPER_CLASS_INTERNAL_TYPE, null);

			// Build marshaller fields
			createMarshallerFields(cw, propertyDescriptorsCopy);

			// Build constructor
			createConstructor(cw, className, propertyDescriptorsCopy);

			// Build Marshaller#marshall method
			createMarshallMethod(cw, className, type, serializationStrategy, propertyDescriptorsCopy);

			// Build Marshaller#unmarshall method
			createUnmarshallMethod(cw, className, type, propertyDescriptorsCopy);

			// Closing class visit
			cw.visitEnd();

			final byte[] bytecode = cw.toByteArray();

			if (debugCacheDirectory != null) {
				File file = new File(debugCacheDirectory, className + ".class");
				FileOutputStream out = new FileOutputStream(file);
				out.write(bytecode);
				out.flush();
				out.close();
			}

			Class<? extends Marshaller> generatedClass = classloader.loadClass(bytecode);
			Constructor<? extends Marshaller> constructor = generatedClass.getConstructor(Class.class, Map.class,
					ClassDescriptorAwareSerializer.class, ObjectInstantiatorFactory.class, List.class);

			constructor.setAccessible(true);
			return constructor.newInstance(type, marshallers, serializer, objectInstantiatorFactory, propertyDescriptorsCopy);
		}
		catch (Exception e) {
			throw new SerializerMarshallerGeneratorException("Marshaller for type " + type + " could not be generated", e);
		}
	}

	private void createMarshallerFields(ClassWriter cw, List<PropertyDescriptor> propertyDescriptors) {
		for (int i = 0; i < propertyDescriptors.size(); i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors.get(i);

			// Write Marshaller field
			FieldVisitor fv = cw.visitField(ACC_FINAL & ACC_PRIVATE, toFinalFieldName("marshaller", propertyDescriptor),
					MARSHALLER_CLASS_DESCRIPTOR, null, null);

			fv.visitEnd();

			// Write PropertyAccessor field
			fv = cw.visitField(ACC_FINAL & ACC_PRIVATE, toFinalFieldName("accessor", propertyDescriptor),
					PROPERTYACCESSOR_CLASS_DESCRIPTOR, null, null);

			fv.visitEnd();
		}
	}

	private void createConstructor(ClassWriter cw, String className, List<PropertyDescriptor> propertyDescriptors) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", MARSHALLER_CONSTRUCTOR_SIGNATURE, null, null);
		mv.visitCode();

		// Load this
		mv.visitVarInsn(ALOAD, 0);

		// Load first parameter type (Class)
		mv.visitVarInsn(ALOAD, 1);

		// Load second parameter type (Map)
		mv.visitVarInsn(ALOAD, 2);

		// Load third parameter type (ClassDescriptorAwaySerializer)
		mv.visitVarInsn(ALOAD, 3);

		// Load fourth parameter type (ObjenesisSerializer)
		mv.visitVarInsn(ALOAD, 4);

		// Call super(Class, Map)
		mv.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS_INTERNAL_TYPE, "<init>", MARSHALLER_SUPER_CONSTRUCTOR_SIGNATURE);

		// Fill fields with marshallers
		for (int i = 0; i < propertyDescriptors.size(); i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors.get(i);
			String fieldName = toFinalFieldName("marshaller", propertyDescriptor);
			mv.visitVarInsn(ALOAD, 0);

			// Load property type
			mv.visitVarInsn(ALOAD, 5);
			mv.visitIntInsn(BIPUSH, i);
			mv.visitMethodInsn(INVOKEINTERFACE, LIST_CLASS_INTERNAL_TYPE, "get", "(I)Ljava/lang/Object;");

			// Store PropertyDescriptor
			mv.visitVarInsn(ASTORE, 6);

			// Define branch
			Label labelNonNull = new Label();

			// Check if marshaller is defined
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 6);
			mv.visitMethodInsn(INVOKEINTERFACE, PROPERTYDESCRIPTOR_CLASS_INTERNAL_TYPE, "getMarshaller",
					PROPERTY_DESCRIPTOR_GET_MARSHALLER_SIGNATURE);
			mv.visitTypeInsn(CHECKCAST, MARSHALLER_CLASS_INTERNAL_TYPE);
			mv.visitVarInsn(ASTORE, 7);
			mv.visitVarInsn(ALOAD, 7);
			mv.visitJumpInsn(IFNONNULL, labelNonNull);

			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 6);
			mv.visitMethodInsn(INVOKEINTERFACE, PROPERTYDESCRIPTOR_CLASS_INTERNAL_TYPE, "getType", "()Ljava/lang/Class;");

			// Search marshaller for property type
			mv.visitMethodInsn(INVOKEVIRTUAL, SUPER_CLASS_INTERNAL_TYPE, "findMarshaller", MARSHALLER_FIND_MARSHALLER_SIGNATURE);
			mv.visitVarInsn(ASTORE, 7);

			// Save marshaller to field
			mv.visitLabel(labelNonNull);
			mv.visitVarInsn(ALOAD, 7);
			mv.visitFieldInsn(PUTFIELD, className, fieldName, MARSHALLER_CLASS_DESCRIPTOR);

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DUP);

			// Push property name to method stack
			mv.visitLdcInsn(propertyDescriptor.getPropertyName());

			// Load property accessor
			mv.visitMethodInsn(INVOKEVIRTUAL, SUPER_CLASS_INTERNAL_TYPE, "getPropertyAccessor",
					MARSHALLER_GET_PROPERTY_ACCESSOR_SIGNATURE);

			// Save PropertyAccessor to field
			mv.visitFieldInsn(PUTFIELD, className, toFinalFieldName("accessor", propertyDescriptor), PROPERTYACCESSOR_CLASS_DESCRIPTOR);
		}

		mv.visitInsn(RETURN);
		mv.visitMaxs(8, 8);
		mv.visitEnd();
	}

	private void createMarshallMethod(ClassWriter cw, String className, Class<?> type,
			SerializationStrategy serializationStrategy, List<PropertyDescriptor> propertyDescriptors) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "marshall", MARSHALLER_MARSHALL_SIGNATURE, null, MARSHALLER_EXCEPTIONS);

		// If element type is not reference capable or SerializationStrategy is
		// not SizeOptimized just prevent generation of code
		if (serializationStrategy == SerializationStrategy.SizeOptimized && ClassUtil.isReferenceCapable(type)) {
			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Load value to method stack
			mv.visitVarInsn(ALOAD, 1);

			// Load type to method stack
			mv.visitVarInsn(ALOAD, 2);

			// Load dataOutput to method stack
			mv.visitVarInsn(ALOAD, 3);

			// Load serializationContext to method stack
			mv.visitVarInsn(ALOAD, 4);

			// Call super.isAlreadyMarshalled(...);
			mv.visitMethodInsn(INVOKEVIRTUAL, SUPER_CLASS_INTERNAL_TYPE, "isAlreadyMarshalled",
					MARSHALLER_IS_ALREADY_MARSHALLED_SIGNATURE);

			// Label if value is not yet marshalled
			Label notYetMarshalled = new Label();

			// Test if already marshalled
			mv.visitJumpInsn(IFEQ, notYetMarshalled);

			// If marshalled - just return
			mv.visitInsn(RETURN);

			// Visit label - if not yet marshalled - marshall it
			mv.visitLabel(notYetMarshalled);
		}

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String fieldName = toFinalFieldName("marshaller", propertyDescriptor);
			Class<?> propertyType = propertyDescriptor.getType();

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Load property marshaller on stack
			mv.visitFieldInsn(GETFIELD, className, fieldName, MARSHALLER_CLASS_DESCRIPTOR);

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Read PropertyAccessor from field
			mv.visitFieldInsn(GETFIELD, className, toFinalFieldName("accessor", propertyDescriptor), PROPERTYACCESSOR_CLASS_DESCRIPTOR);

			// Load property type
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKEINTERFACE, PROPERTYACCESSOR_CLASS_INTERNAL_TYPE, "getType", OBJECT_GET_CLASS_SIGNATURE);
			mv.visitVarInsn(ASTORE, 5);

			// Load value to method stack
			mv.visitVarInsn(ALOAD, 1);

			// Load value by type on stack
			visitPropertyAccessorRead(propertyType, mv);

			// If type is primitive add some "autoboxing" magic
			if (propertyType.isPrimitive()) {
				visitWrapperAutoboxing(propertyType, mv);
			}

			// Load type to method stack
			mv.visitVarInsn(ALOAD, 5);

			// Load DataOutput to method stack
			mv.visitVarInsn(ALOAD, 3);

			// Load SerializationContext to method stack
			mv.visitVarInsn(ALOAD, 4);

			// Call Marshaller#marshall on properties marshaller
			mv.visitMethodInsn(INVOKEINTERFACE, MARSHALLER_CLASS_INTERNAL_TYPE, "marshall", MARSHALLER_MARSHALL_SIGNATURE);
		}

		// Add Return instruction
		mv.visitInsn(RETURN);

		// End visiting
		mv.visitMaxs(6, 6);
		mv.visitEnd();
	}

	private void createUnmarshallMethod(ClassWriter cw, String className, Class<?> type,
			List<PropertyDescriptor> propertyDescriptors) {

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "unmarshall", MARSHALLER_UNMARSHALL_SIGNATURE, null, MARSHALLER_EXCEPTIONS);

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String fieldName = toFinalFieldName("marshaller", propertyDescriptor);
			Class<?> propertyType = propertyDescriptor.getType();

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Read PropertyAccessor from field
			mv.visitFieldInsn(GETFIELD, className, toFinalFieldName("accessor", propertyDescriptor), PROPERTYACCESSOR_CLASS_DESCRIPTOR);

			// Store PropertyAccessor for later use
			mv.visitVarInsn(ASTORE, 5);

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Load property marshaller to method stack
			mv.visitFieldInsn(GETFIELD, className, fieldName, MARSHALLER_CLASS_DESCRIPTOR);

			// Load PropertyAccessor to method stack
			mv.visitVarInsn(ALOAD, 5);

			// Load Type from PropertyAccessor to method stack
			mv.visitMethodInsn(INVOKEINTERFACE, PROPERTYACCESSOR_CLASS_INTERNAL_TYPE, "getType", OBJECT_GET_CLASS_SIGNATURE);

			// Load DataInput to method stack
			mv.visitVarInsn(ALOAD, 3);

			// Load SerializationContext to method stack
			mv.visitVarInsn(ALOAD, 4);

			// Call Marshaller#unmarshall on properties marshaller
			mv.visitMethodInsn(INVOKEINTERFACE, MARSHALLER_CLASS_INTERNAL_TYPE, "unmarshall",
					MARSHALLER_BASE_UNMARSHALL_SIGNATURE);

			// Save value
			mv.visitVarInsn(ASTORE, 6);

			// Load PropertyAccessor to method stack
			mv.visitVarInsn(ALOAD, 5);

			// Load instance to method stack
			mv.visitVarInsn(ALOAD, 1);

			// Load value to method stack
			mv.visitVarInsn(ALOAD, 6);

			// If type is primitive add some "autoboxing" magic
			if (propertyType.isPrimitive()) {
				visitPrimitiveAutoboxing(propertyType, mv);
			}

			// Call PropertyAccessor#writeX
			visitPropertyAccessorWrite(propertyType, mv);
		}

		// Load instance to method stack
		mv.visitVarInsn(ALOAD, 1);

		// Add Return statement
		visitReturn(type, mv);

		// End visiting
		mv.visitMaxs(7, 7);
		mv.visitEnd();
	}

	private void visitReturn(Class<?> type, MethodVisitor mv) {
		int returnOpcode = ARETURN;

		if (type == boolean.class) {
			returnOpcode = IRETURN;
		}
		else if (type == byte.class) {
			returnOpcode = IRETURN;
		}
		else if (type == char.class) {
			returnOpcode = IRETURN;
		}
		else if (type == short.class) {
			returnOpcode = IRETURN;
		}
		else if (type == int.class) {
			returnOpcode = IRETURN;
		}
		else if (type == long.class) {
			returnOpcode = LRETURN;
		}
		else if (type == float.class) {
			returnOpcode = FRETURN;
		}
		else if (type == double.class) {
			returnOpcode = DRETURN;
		}
		else {
			returnOpcode = ARETURN;
		}

		mv.visitInsn(returnOpcode);
	}

	private void visitPropertyAccessorRead(Class<?> type, MethodVisitor mv) {
		String methodName = null;
		String methodSignature = null;

		if (type == boolean.class) {
			methodName = "readBoolean";
			methodSignature = PROPERTY_ACCESSOR_READ_BOOLEAN_SIGNATURE;
		}
		else if (type == byte.class) {
			methodName = "readByte";
			methodSignature = PROPERTY_ACCESSOR_READ_BYTE_SIGNATURE;
		}
		else if (type == char.class) {
			methodName = "readChar";
			methodSignature = PROPERTY_ACCESSOR_READ_CHAR_SIGNATURE;
		}
		else if (type == short.class) {
			methodName = "readShort";
			methodSignature = PROPERTY_ACCESSOR_READ_SHORT_SIGNATURE;
		}
		else if (type == int.class) {
			methodName = "readInt";
			methodSignature = PROPERTY_ACCESSOR_READ_INT_SIGNATURE;
		}
		else if (type == long.class) {
			methodName = "readLong";
			methodSignature = PROPERTY_ACCESSOR_READ_LONG_SIGNATURE;
		}
		else if (type == float.class) {
			methodName = "readFloat";
			methodSignature = PROPERTY_ACCESSOR_READ_FLOAT_SIGNATURE;
		}
		else if (type == double.class) {
			methodName = "readDouble";
			methodSignature = PROPERTY_ACCESSOR_READ_DOUBLE_SIGNATURE;
		}
		else {
			methodName = "readObject";
			methodSignature = PROPERTY_ACCESSOR_READ_OBJECT_SIGNATURE;
		}

		mv.visitMethodInsn(INVOKEINTERFACE, PROPERTYACCESSOR_CLASS_INTERNAL_TYPE, methodName, methodSignature);
	}

	private void visitPropertyAccessorWrite(Class<?> type, MethodVisitor mv) {
		String methodName = null;
		String methodSignature = null;

		if (type == boolean.class) {
			methodName = "writeBoolean";
			methodSignature = PROPERTY_ACCESSOR_WRITE_BOOLEAN_SIGNATURE;
		}
		else if (type == byte.class) {
			methodName = "writeByte";
			methodSignature = PROPERTY_ACCESSOR_WRITE_BYTE_SIGNATURE;
		}
		else if (type == char.class) {
			methodName = "writeChar";
			methodSignature = PROPERTY_ACCESSOR_WRITE_CHAR_SIGNATURE;
		}
		else if (type == short.class) {
			methodName = "writeShort";
			methodSignature = PROPERTY_ACCESSOR_WRITE_SHORT_SIGNATURE;
		}
		else if (type == int.class) {
			methodName = "writeInt";
			methodSignature = PROPERTY_ACCESSOR_WRITE_INT_SIGNATURE;
		}
		else if (type == long.class) {
			methodName = "writeLong";
			methodSignature = PROPERTY_ACCESSOR_WRITE_LONG_SIGNATURE;
		}
		else if (type == float.class) {
			methodName = "writeFloat";
			methodSignature = PROPERTY_ACCESSOR_WRITE_FLOAT_SIGNATURE;
		}
		else if (type == double.class) {
			methodName = "writeDouble";
			methodSignature = PROPERTY_ACCESSOR_WRITE_DOUBLE_SIGNATURE;
		}
		else {
			methodName = "writeObject";
			methodSignature = PROPERTY_ACCESSOR_WRITE_OBJECT_SIGNATURE;
		}

		mv.visitMethodInsn(INVOKEINTERFACE, PROPERTYACCESSOR_CLASS_INTERNAL_TYPE, methodName, methodSignature);
	}

	private void visitPrimitiveAutoboxing(Class<?> type, MethodVisitor mv) {
		if (type == boolean.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
		}
		else if (type == byte.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
		}
		else if (type == char.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
		}
		else if (type == short.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
		}
		else if (type == int.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
		}
		else if (type == long.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
		}
		else if (type == float.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
		}
		else if (type == double.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
		}
	}

	private void visitWrapperAutoboxing(Class<?> type, MethodVisitor mv) {
		if (type == boolean.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", BOOLEAN_VALUE_OF_SIGNATURE);
		}
		else if (type == byte.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", BYTE_VALUE_OF_SIGNATURE);
		}
		else if (type == char.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", CHAR_VALUE_OF_SIGNATURE);
		}
		else if (type == short.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", SHORT_VALUE_OF_SIGNATURE);
		}
		else if (type == int.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", INT_VALUE_OF_SIGNATURE);
		}
		else if (type == long.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", LONG_VALUE_OF_SIGNATURE);
		}
		else if (type == float.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", FLOAT_VALUE_OF_SIGNATURE);
		}
		else if (type == double.class) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", DOUBLE_VALUE_OF_SIGNATURE);
		}
	}

	private String toFinalFieldName(String prefix, PropertyDescriptor propertyDescriptor) {
		return new StringBuilder(prefix.toUpperCase()).append("_").append(propertyDescriptor.getPropertyName()
				.toUpperCase()).append("_LIGHTNING").toString();
	}

	protected void visitSystemOutPrintln(MethodVisitor mv, int stackPosition) {
		mv.visitVarInsn(ASTORE, stackPosition);
		mv.visitFieldInsn(GETSTATIC, Type.getType(System.class).getInternalName(), "out", Type.getType(PrintStream.class)
				.getDescriptor());
		mv.visitVarInsn(ALOAD, stackPosition);
		mv.visitMethodInsn(INVOKEVIRTUAL, Type.getType(Object.class).getInternalName(), "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKEVIRTUAL, Type.getType(PrintStream.class).getInternalName(), "println", "(Ljava/lang/String;)V");
		mv.visitVarInsn(ALOAD, stackPosition);
	}
}
