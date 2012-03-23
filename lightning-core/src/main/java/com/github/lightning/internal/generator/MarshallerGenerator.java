package com.github.lightning.internal.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.lightning.Marshaller;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.SerializerMarshallerGeneratorException;
import com.github.lightning.internal.ClassDescriptorAwareSerializer;
import com.github.lightning.internal.instantiator.ObjenesisSerializer;

public class MarshallerGenerator implements Opcodes, GeneratorConstants {

	private final GeneratorClassLoader classloader = CreateClassLoader.createClassLoader(getClass().getClassLoader());

	public Marshaller generateMarshaller(Class<?> type, List<PropertyDescriptor> propertyDescriptors,
			Map<Class<?>, Marshaller> marshallers, ClassDescriptorAwareSerializer serializer,
			ObjenesisSerializer objenesisSerializer) {

		try {
			ClassWriter cw = new ClassWriter(0);

			// Copy properties and sort them by name
			List<PropertyDescriptor> propertyDescriptorsCopy = new ArrayList<PropertyDescriptor>(propertyDescriptors);
			Collections.sort(propertyDescriptorsCopy);

			// Build className e.g. "SomeTypeMarshaller$$X$$Lightning"
			String className = new StringBuilder(type.getSimpleName()).append("Marshaller$$")
					.append(GENEREATED_CLASS_ID.getAndIncrement()).append("$$Lightning").toString();

			// Build class
			cw.visit(V1_6, ACC_PUBLIC & ACC_SUPER, className, className, SUPER_CLASS_INTERNAL_TYPE, null);

			// Build marshaller fields
			createMarshallerFields(cw, propertyDescriptorsCopy);

			// Build constructor
			createConstructor(cw, className, propertyDescriptorsCopy);

			// Build Marshaller#marshall method
			createMarshallMethod(cw, className, propertyDescriptorsCopy);

			// Build Marshaller#unmarshall method
			createUnmarshallMethod(cw, className, type, propertyDescriptorsCopy);

			// Closing class visit
			cw.visitEnd();

			final byte[] bytecode = cw.toByteArray();

			File file = new File(className + ".class");
			FileOutputStream out = new FileOutputStream(file);
			out.write(bytecode);
			out.flush();
			out.close();

			Class<? extends Marshaller> generatedClass = classloader.loadClass(bytecode);
			Constructor<? extends Marshaller> constructor = generatedClass.getConstructor(Class.class, Map.class,
					ClassDescriptorAwareSerializer.class, ObjenesisSerializer.class);

			return constructor.newInstance(type, marshallers, serializer, objenesisSerializer);
		}
		catch (Exception e) {
			throw new SerializerMarshallerGeneratorException("Marshaller for type " + type + " could not be generated", e);
		}
	}

	private void createMarshallerFields(ClassWriter cw, List<PropertyDescriptor> propertyDescriptors) {
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			FieldVisitor fv = cw.visitField(ACC_FINAL & ACC_PRIVATE, toFinalFieldName(propertyDescriptor),
					Type.getType(propertyDescriptor.getType()).getDescriptor(), null, null);

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
		
		//Load fourth parameter type (ObjenesisSerializer)
		mv.visitVarInsn(ALOAD, 4);

		// Call super(Class, Map)
		mv.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS_INTERNAL_TYPE, "<init>", MARSHALLER_CONSTRUCTOR_SIGNATURE);

		// Fill fields with marshallers
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String fieldName = toFinalFieldName(propertyDescriptor);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, SUPER_CLASS_INTERNAL_TYPE, "findMarshaller", MARSHALLER_FIND_MARSHALLER_SIGNATURE);
			mv.visitFieldInsn(PUTFIELD, className, fieldName, Type.getType(propertyDescriptor.getType()).getDescriptor());
		}

		mv.visitInsn(RETURN);
		mv.visitMaxs(5, 5);
		mv.visitEnd();
	}

	private void createMarshallMethod(ClassWriter cw, String className, List<PropertyDescriptor> propertyDescriptors) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "marshall", MARSHALLER_MARSHALL_SIGNATURE, null, MARSHALLER_EXCEPTIONS);

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String fieldName = toFinalFieldName(propertyDescriptor);
			Class<?> type = propertyDescriptor.getType();

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Load property marshaller on stack
			mv.visitFieldInsn(GETFIELD, className, fieldName, Type.getType(propertyDescriptor.getType()).getDescriptor());

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Push property name to method stack
			mv.visitLdcInsn(propertyDescriptor.getPropertyName());

			// Load property accessor
			mv.visitMethodInsn(INVOKEVIRTUAL, SUPER_CLASS_INTERNAL_TYPE, "getPropertyAccessor",
					MARSHALLER_GET_PROPERTY_ACCESSOR_SIGNATURE);

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);
			
			// Load value by type on stack
			visitPropertyAccessorRead(type, mv);

			// If type is primitive add some "autoboxing" magic
			if (type.isPrimitive()) {
				visitWrapperAutoboxing(type, mv);
			}

			// Save value to stack property
			mv.visitVarInsn(ASTORE, 4);

			// Load it two times to method stack
			mv.visitVarInsn(ALOAD, 4);
			mv.visitVarInsn(ALOAD, 4);

			// Call getClass()
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", OBJECT_GET_CLASS_SIGNATURE);

			// Load DataOutput to method stack
			mv.visitVarInsn(ALOAD, 3);

			// Call Marshaller#marshall on properties marshaller
			mv.visitMethodInsn(INVOKEINTERFACE, MARSHALLER_CLASS_INTERNAL_TYPE, "marshall", MARSHALLER_MARSHALL_SIGNATURE);
		}

		// Add Return instruction
		mv.visitInsn(RETURN);

		// End visiting
		mv.visitMaxs(5, 5);
		mv.visitEnd();
	}

	private void createUnmarshallMethod(ClassWriter cw, String className, Class<?> type,
			List<PropertyDescriptor> propertyDescriptors) {

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "unmarshall", MARSHALLER_UNMARSHALL_SIGNATURE, null, MARSHALLER_EXCEPTIONS);

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String fieldName = toFinalFieldName(propertyDescriptor);
			Class<?> propertyType = propertyDescriptor.getType();

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Push property name to method stack
			mv.visitLdcInsn(propertyDescriptor.getPropertyName());
			
			// Load property accessor
			mv.visitMethodInsn(INVOKEVIRTUAL, SUPER_CLASS_INTERNAL_TYPE, "getPropertyAccessor",
					MARSHALLER_GET_PROPERTY_ACCESSOR_SIGNATURE);

			// Store PropertyAccessor for later use
			mv.visitVarInsn(ASTORE, 4);

			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);
			
			// Load this to method stack
			mv.visitVarInsn(ALOAD, 0);

			// Load property marshaller to method stack
			mv.visitFieldInsn(GETFIELD, className, fieldName, Type.getType(propertyDescriptor.getType()).getDescriptor());

			// Load PropertyAccessor to method stack
			mv.visitVarInsn(ALOAD, 4);

			// Load Type from PropertyAccessor to method stack
			mv.visitMethodInsn(INVOKEINTERFACE, PROPERTYACCESSOR_CLASS_INTERNAL_TYPE, "getType", OBJECT_GET_CLASS_SIGNATURE);

			// Load DataInput to method stack
			mv.visitVarInsn(ALOAD, 3);

			// Call Marshaller#unmarshall on properties marshaller
			mv.visitMethodInsn(INVOKEINTERFACE, MARSHALLER_CLASS_INTERNAL_TYPE, "unmarshall", MARSHALLER_BASE_UNMARSHALL_SIGNATURE);

			// Save value
			mv.visitVarInsn(ASTORE, 5);

			// Load PropertyAccessor to method stack
			mv.visitVarInsn(ALOAD, 4);

			// Load instance to method stack
			mv.visitVarInsn(ALOAD, 1);

			// Load value to method stack
			mv.visitVarInsn(ALOAD, 5);

			// If type is primitive add some "autoboxing" magic
			if (propertyType.isPrimitive()) {
				visitPrimitiveAutoboxing(propertyType, mv);
			}

			// Call PropertyAccessor#writeX
			visitPropertyAccessorWrite(propertyType, mv);
		}

		// Add Return statement
		visitReturn(type, mv);

		// End visiting
		mv.visitMaxs(6, 6);
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
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "valueOf", BOOLEAN_VALUE_OF_SIGNATURE);
		}
		else if (type == byte.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "valueOf", BYTE_VALUE_OF_SIGNATURE);
		}
		else if (type == char.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "valueOf", CHAR_VALUE_OF_SIGNATURE);
		}
		else if (type == short.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "valueOf", SHORT_VALUE_OF_SIGNATURE);
		}
		else if (type == int.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "valueOf", INT_VALUE_OF_SIGNATURE);
		}
		else if (type == long.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "valueOf", LONG_VALUE_OF_SIGNATURE);
		}
		else if (type == float.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "valueOf", FLOAT_VALUE_OF_SIGNATURE);
		}
		else if (type == double.class) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "valueOf", DOUBLE_VALUE_OF_SIGNATURE);
		}
	}

	private String toFinalFieldName(PropertyDescriptor propertyDescriptor) {
		return new StringBuilder("PROPERTY_").append(propertyDescriptor.getPropertyName().toUpperCase()).append("_LIGHTNING")
				.toString();
	}

	private String toInternalRepresentation(String className) {
		return new StringBuilder("L").append(className).append(";").toString();
	}
}
