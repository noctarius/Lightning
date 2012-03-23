package com.github.lightning.internal.generator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.asm.Type;

import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.Marshaller;
import com.github.lightning.PropertyAccessor;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.ClassDescriptorAwareSerializer;
import com.github.lightning.internal.instantiator.ObjenesisSerializer;

public interface GeneratorConstants {

	static String MARSHALLER_MARSHALL_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.getType(Class.class),
					Type.getType(DataOutput.class), Type.getType(ClassDefinitionContainer.class) });

	static String MARSHALLER_BASE_UNMARSHALL_SIGNATURE = Type.getMethodDescriptor(Type.getType(Object.class),
			new Type[] { Type.getType(Class.class), Type.getType(DataInput.class), Type.getType(ClassDefinitionContainer.class) });

	static String MARSHALLER_UNMARSHALL_SIGNATURE = Type.getMethodDescriptor(Type.getType(Object.class),
			new Type[] { Type.getType(Object.class), Type.getType(Class.class),
					Type.getType(DataInput.class), Type.getType(ClassDefinitionContainer.class) });

	static String MARSHALLER_FIND_MARSHALLER_SIGNATURE = Type.getMethodDescriptor(Type.getType(Marshaller.class),
			new Type[] { Type.getType(Class.class) });

	static String MARSHALLER_GET_PROPERTY_ACCESSOR_SIGNATURE = Type.getMethodDescriptor(Type.getType(PropertyAccessor.class),
			new Type[] { Type.getType(String.class) });

	static String MARSHALLER_CONSTRUCTOR_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Class.class), Type.getType(Map.class), Type.getType(ClassDescriptorAwareSerializer.class),
					Type.getType(ObjenesisSerializer.class), Type.getType(List.class) });

	static String MARSHALLER_SUPER_CONSTRUCTOR_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Class.class), Type.getType(Map.class), Type.getType(ClassDescriptorAwareSerializer.class),
					Type.getType(ObjenesisSerializer.class) });

	static String PROPERTY_DESCRIPTOR_GET_MARSHALLER_SIGNATURE = Type.getMethodDescriptor(Type.getType(Marshaller.class), new Type[0]);

	static String OBJECT_GET_CLASS_SIGNATURE = Type.getMethodDescriptor(Type.getType(Class.class), new Type[0]);

	static String SUPER_CLASS_INTERNAL_TYPE = Type.getType(AbstractGeneratedMarshaller.class).getInternalName();
	static String MARSHALLER_CLASS_INTERNAL_TYPE = Type.getType(Marshaller.class).getInternalName();
	static String IOEXCEPTION_CLASS_INTERNAL_TYPE = Type.getType(IOException.class).getInternalName();
	static String LIST_CLASS_INTERNAL_TYPE = Type.getType(List.class).getInternalName();
	static String PROPERTYACCESSOR_CLASS_INTERNAL_TYPE = Type.getType(PropertyAccessor.class).getInternalName();
	static String PROPERTYDESCRIPTOR_CLASS_INTERNAL_TYPE = Type.getType(PropertyDescriptor.class).getInternalName();

	static String MARSHALLER_CLASS_DESCRIPTOR = Type.getType(Marshaller.class).getDescriptor();
	static String PROPERTYDESCRIPTOR_CLASS_DESCRIPTOR = Type.getType(PropertyDescriptor.class).getDescriptor();

	static String[] MARSHALLER_EXCEPTIONS = { IOEXCEPTION_CLASS_INTERNAL_TYPE };

	static AtomicLong GENEREATED_CLASS_ID = new AtomicLong();

	static String PROPERTY_DESCRIPTOR_FIELD_NAME = "PROPERTY_DESCRIPTORS";

	static String PROPERTY_ACCESSOR_READ_BOOLEAN_SIGNATURE = Type.getMethodDescriptor(Type.BOOLEAN_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_BYTE_SIGNATURE = Type.getMethodDescriptor(Type.BYTE_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_CHAR_SIGNATURE = Type.getMethodDescriptor(Type.CHAR_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_SHORT_SIGNATURE = Type.getMethodDescriptor(Type.SHORT_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_INT_SIGNATURE = Type.getMethodDescriptor(Type.INT_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_LONG_SIGNATURE = Type.getMethodDescriptor(Type.LONG_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_FLOAT_SIGNATURE = Type.getMethodDescriptor(Type.FLOAT_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_DOUBLE_SIGNATURE = Type.getMethodDescriptor(Type.DOUBLE_TYPE,
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_READ_OBJECT_SIGNATURE = Type.getMethodDescriptor(Type.getType(Object.class),
			new Type[] { Type.getType(Object.class) });

	static String PROPERTY_ACCESSOR_WRITE_BOOLEAN_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.BOOLEAN_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_BYTE_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.BYTE_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_CHAR_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.CHAR_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_SHORT_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.SHORT_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_INT_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.INT_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_LONG_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.LONG_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_FLOAT_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.FLOAT_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_DOUBLE_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.DOUBLE_TYPE });

	static String PROPERTY_ACCESSOR_WRITE_OBJECT_SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[] { Type.getType(Object.class), Type.getType(Object.class) });

	static String BOOLEAN_VALUE_OF_SIGNATURE = Type.getMethodDescriptor(Type.getType(Boolean.class),
			new Type[] { Type.BOOLEAN_TYPE });

	static String BYTE_VALUE_OF_SIGNATURE = Type.getMethodDescriptor(Type.getType(Byte.class), new Type[] { Type.BYTE_TYPE });

	static String CHAR_VALUE_OF_SIGNATURE = Type
			.getMethodDescriptor(Type.getType(Character.class), new Type[] { Type.CHAR_TYPE });

	static String SHORT_VALUE_OF_SIGNATURE = Type.getMethodDescriptor(Type.getType(Short.class), new Type[] { Type.SHORT_TYPE });

	static String INT_VALUE_OF_SIGNATURE = Type.getMethodDescriptor(Type.getType(Integer.class), new Type[] { Type.INT_TYPE });

	static String LONG_VALUE_OF_SIGNATURE = Type.getMethodDescriptor(Type.getType(Long.class), new Type[] { Type.LONG_TYPE });

	static String FLOAT_VALUE_OF_SIGNATURE = Type.getMethodDescriptor(Type.getType(Float.class), new Type[] { Type.FLOAT_TYPE });

	static String DOUBLE_VALUE_OF_SIGNATURE = Type.getMethodDescriptor(Type.getType(Double.class),
			new Type[] { Type.DOUBLE_TYPE });
}
