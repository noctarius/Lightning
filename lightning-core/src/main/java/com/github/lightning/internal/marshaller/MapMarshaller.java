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
package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.lightning.Marshaller;
import com.github.lightning.SerializationContext;
import com.github.lightning.TypeBindableMarshaller;
import com.github.lightning.base.AbstractMarshaller;
import com.github.lightning.metadata.ClassDefinition;

public class MapMarshaller extends AbstractMarshaller implements TypeBindableMarshaller {

	private final Class<?> mapKeyType;
	private final Class<?> mapValueType;

	private Marshaller mapKeyTypeMarshaller;
	private Marshaller mapValueTypeMarshaller;

	public MapMarshaller() {
		this(null, null);
	}

	private MapMarshaller(Class<?> mapKeyType, Class<?> mapValueType) {
		this.mapKeyType = mapKeyType;
		this.mapValueType = mapValueType;
	}

	@Override
	public boolean acceptType(Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput, SerializationContext serializationContext) throws IOException {
		writePossibleNull(value, dataOutput);

		Map<?, ?> map = (Map<?, ?>) value;
		dataOutput.writeInt(map.size());
		for (Entry<?, ?> entry : map.entrySet()) {
			Marshaller keyMarshaller;
			Marshaller valueMarshaller;
			if (mapKeyType != null) {
				ensureMarshallersInitialized(serializationContext);
				keyMarshaller = mapKeyTypeMarshaller;
				valueMarshaller = mapValueTypeMarshaller;
			}
			else {
				keyMarshaller = entry.getKey() != null ? serializationContext.findMarshaller(entry.getKey().getClass()) : null;
				valueMarshaller = entry.getValue() != null ? serializationContext.findMarshaller(entry.getValue().getClass()) : null;
			}

			if (writePossibleNull(entry.getKey(), dataOutput)) {
				ClassDefinition keyClassDefinition = serializationContext.getClassDefinitionContainer().getClassDefinitionByType(entry.getKey().getClass());
				dataOutput.writeLong(keyClassDefinition.getId());
				keyMarshaller.marshall(entry.getKey(), entry.getKey().getClass(), dataOutput, serializationContext);
			}

			if (writePossibleNull(entry.getValue(), dataOutput)) {
				ClassDefinition valueClassDefinition = serializationContext.getClassDefinitionContainer().getClassDefinitionByType(entry.getValue().getClass());
				dataOutput.writeLong(valueClassDefinition.getId());
				valueMarshaller.marshall(entry.getValue(), entry.getValue().getClass(), dataOutput, serializationContext);
			}
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <V> V unmarshall(Class<?> type, DataInput dataInput, SerializationContext serializationContext) throws IOException {
		if (isNull(dataInput)) {
			return null;
		}

		int size = dataInput.readInt();
		Map map = new LinkedHashMap(size);
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Object key = null;
				if (!isNull(dataInput)) {
					long keyClassId = dataInput.readLong();
					ClassDefinition keyClassDefinition = serializationContext.getClassDefinitionContainer().getClassDefinitionById(keyClassId);

					Marshaller keyMarshaller;
					if (mapKeyType != null) {
						ensureMarshallersInitialized(serializationContext);
						keyMarshaller = mapKeyTypeMarshaller;
					}
					else {
						keyMarshaller = serializationContext.findMarshaller(keyClassDefinition.getType());
					}

					key = keyMarshaller.unmarshall(keyClassDefinition.getType(), dataInput, serializationContext);
				}

				Object value = null;
				if (!isNull(dataInput)) {
					long valueClassId = dataInput.readLong();
					ClassDefinition valueClassDefinition = serializationContext.getClassDefinitionContainer().getClassDefinitionById(valueClassId);

					Marshaller valueMarshaller;
					if (mapKeyType != null) {
						ensureMarshallersInitialized(serializationContext);
						valueMarshaller = mapValueTypeMarshaller;
					}
					else {
						valueMarshaller = serializationContext.findMarshaller(valueClassDefinition.getType());
					}

					value = valueMarshaller.unmarshall(valueClassDefinition.getType(), dataInput, serializationContext);
				}

				map.put(key, value);
			}
		}

		return (V) map;
	}

	@Override
	public Marshaller bindType(Field property) {
		Type genericType = property.getGenericType();
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;
			Type[] types = type.getActualTypeArguments();
			if (types.length == 2) {
				Class<?> mapKeyType = (Class<?>) types[0];
				Class<?> mapValueType = (Class<?>) types[1];
				return new MapMarshaller(mapKeyType, mapValueType);
			}
		}

		return new MapMarshaller();
	}

	private void ensureMarshallersInitialized(SerializationContext serializationContext) {
		if (mapKeyTypeMarshaller != null && mapValueTypeMarshaller != null)
			return;

		mapKeyTypeMarshaller = serializationContext.findMarshaller(mapKeyType);
		mapValueTypeMarshaller = serializationContext.findMarshaller(mapValueType);
	}
}
