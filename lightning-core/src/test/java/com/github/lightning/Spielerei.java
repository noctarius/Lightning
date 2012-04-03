package com.github.lightning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.metadata.ArrayPropertyAccessor;

public class Spielerei {

	private Marshaller arrayMarshaller;
	private ArrayPropertyAccessor propertyAccessor;

	public void marshall(Object value, Class<?> type, DataOutput dataOutput, SerializationContext serializationContext) throws IOException {
		Object propertyValue = propertyAccessor.readObject(value);

		String[] array = (String[]) propertyValue;
		dataOutput.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			Object arrayValue = propertyAccessor.readObject(value, i);
			arrayMarshaller.marshall(arrayValue, propertyAccessor.getType().getComponentType(), dataOutput, serializationContext);
		}
	}

	public <V> V unmarshall(V instance, Class<?> type, DataInput dataInput, SerializationContext serializationContext) throws IOException {
		int size = dataInput.readInt();
		String[] array = new String[size];
		for (int i = 0; i < size; i++) {
			Class<?> componentType = propertyAccessor.getType().getComponentType();
			Object arrayValue = arrayMarshaller.unmarshall(componentType, dataInput, serializationContext);
			propertyAccessor.writeObject(array, i, arrayValue);
		}
		return (V) array;
	}
}
