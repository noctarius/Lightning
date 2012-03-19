package com.github.lightning.internal.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.lightning.PropertyAccessor;
import com.github.lightning.internal.util.BeanUtil;

public class ReflectASMPropertyAccessorFactory implements PropertyAccessorFactory {

	private final Map<Class<?>, MethodAccess> methodAccessCache = new HashMap<Class<?>, MethodAccess>();
	private final Map<Class<?>, FieldAccess> fieldAccessCache = new HashMap<Class<?>, FieldAccess>();

	@Override
	public PropertyAccessor fieldAccess(Field field) {
		try {
			return buildForField(field);
		}
		catch (IllegalArgumentException e) {
			// If field is not public
			return null;
		}
	}

	@Override
	public PropertyAccessor methodAccess(Method method) {
		try {
			return buildForMethod(method);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	private FieldAccess getFieldAccess(Field field) {
		Class<?> declaringClass = field.getDeclaringClass();

		FieldAccess fieldAccess = fieldAccessCache.get(declaringClass);
		if (fieldAccess != null) {
			return fieldAccess;
		}

		fieldAccess = FieldAccess.get(declaringClass);
		fieldAccessCache.put(declaringClass, fieldAccess);

		return fieldAccess;
	}

	private MethodAccess getMethodAccess(Method method) {
		Class<?> declaringClass = method.getDeclaringClass();

		MethodAccess methodAccess = methodAccessCache.get(declaringClass);
		if (methodAccess != null) {
			return methodAccess;
		}

		methodAccess = MethodAccess.get(declaringClass);
		methodAccessCache.put(declaringClass, methodAccess);

		return methodAccess;
	}

	private PropertyAccessor buildForField(Field field) {
		final FieldAccess fieldAccess = getFieldAccess(field);
		final int fieldIndex = fieldAccess.getIndex(field.getName());
		return new FieldPropertyAccessor(field) {

			@Override
			public void writeShort(Object instance, short value) {
				writeObject(instance, value);
			}

			@Override
			public <T> void writeObject(Object instance, T value) {
				fieldAccess.set(instance, fieldIndex, value);
			}

			@Override
			public void writeLong(Object instance, long value) {
				writeObject(instance, value);
			}

			@Override
			public void writeInt(Object instance, int value) {
				writeObject(instance, value);
			}

			@Override
			public void writeFloat(Object instance, float value) {
				writeObject(instance, value);
			}

			@Override
			public void writeDouble(Object instance, double value) {
				writeObject(instance, value);
			}

			@Override
			public void writeChar(Object instance, char value) {
				writeObject(instance, value);
			}

			@Override
			public void writeByte(Object instance, byte value) {
				writeObject(instance, value);
			}

			@Override
			public void writeBoolean(Object instance, boolean value) {
				writeObject(instance, value);
			}

			@Override
			public short readShort(Object instance) {
				return readObject(instance);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObject(Object instance) {
				return (T) fieldAccess.get(instance, fieldIndex);
			}

			@Override
			public long readLong(Object instance) {
				return readObject(instance);
			}

			@Override
			public int readInt(Object instance) {
				return readObject(instance);
			}

			@Override
			public float readFloat(Object instance) {
				return readObject(instance);
			}

			@Override
			public double readDouble(Object instance) {
				return readObject(instance);
			}

			@Override
			public char readChar(Object instance) {
				return readObject(instance);
			}

			@Override
			public byte readByte(Object instance) {
				return readObject(instance);
			}

			@Override
			public boolean readBoolean(Object instance) {
				return readObject(instance);
			}
		};
	}

	private PropertyAccessor buildForMethod(Method method) {
		final MethodAccess methodAccess = getMethodAccess(method);

		Method getter = BeanUtil.findGetterMethod(method);
		Method setter = BeanUtil.findSetterMethod(method);

		final int getterMethodIndex = methodAccess.getIndex(getter.getName(), method.getParameterTypes());
		final int setterMethodIndex = methodAccess.getIndex(setter.getName(), method.getParameterTypes());

		return new MethodPropertyAccessor(setter, getter) {

			@Override
			public void writeShort(Object instance, short value) {
				writeObject(instance, value);
			}

			@Override
			public <T> void writeObject(Object instance, T value) {
				methodAccess.invoke(instance, setterMethodIndex, value);
			}

			@Override
			public void writeLong(Object instance, long value) {
				writeObject(instance, value);
			}

			@Override
			public void writeInt(Object instance, int value) {
				writeObject(instance, value);
			}

			@Override
			public void writeFloat(Object instance, float value) {
				writeObject(instance, value);
			}

			@Override
			public void writeDouble(Object instance, double value) {
				writeObject(instance, value);
			}

			@Override
			public void writeChar(Object instance, char value) {
				writeObject(instance, value);
			}

			@Override
			public void writeByte(Object instance, byte value) {
				writeObject(instance, value);
			}

			@Override
			public void writeBoolean(Object instance, boolean value) {
				writeObject(instance, value);
			}

			@Override
			public short readShort(Object instance) {
				return readObject(instance);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObject(Object instance) {
				return (T) methodAccess.invoke(instance, getterMethodIndex);
			}

			@Override
			public long readLong(Object instance) {
				return readObject(instance);
			}

			@Override
			public int readInt(Object instance) {
				return readObject(instance);
			}

			@Override
			public float readFloat(Object instance) {
				return readObject(instance);
			}

			@Override
			public double readDouble(Object instance) {
				return readObject(instance);
			}

			@Override
			public char readChar(Object instance) {
				return readObject(instance);
			}

			@Override
			public byte readByte(Object instance) {
				return readObject(instance);
			}

			@Override
			public boolean readBoolean(Object instance) {
				return readObject(instance);
			}
		};
	}
}
