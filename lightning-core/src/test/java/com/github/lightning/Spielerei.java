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
