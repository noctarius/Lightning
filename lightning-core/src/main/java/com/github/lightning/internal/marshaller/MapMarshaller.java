package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
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
		return List.class.isAssignableFrom(type);
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
				keyMarshaller = serializationContext.findMarshaller(entry.getKey().getClass());
				valueMarshaller = serializationContext.findMarshaller(entry.getValue().getClass());
			}

			ClassDefinition keyClassDefinition = serializationContext.getClassDefinitionContainer()
					.getClassDefinitionByType(entry.getKey().getClass());

			ClassDefinition valueClassDefinition = serializationContext.getClassDefinitionContainer()
					.getClassDefinitionByType(entry.getValue().getClass());

			dataOutput.writeLong(keyClassDefinition.getId());
			dataOutput.writeLong(valueClassDefinition.getId());
			keyMarshaller.marshall(entry.getKey(), entry.getKey().getClass(), dataOutput, serializationContext);
			valueMarshaller.marshall(entry.getValue(), entry.getValue().getClass(), dataOutput, serializationContext);
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
			long keyClassId = dataInput.readLong();
			long valueClassId = dataInput.readLong();

			ClassDefinition keyClassDefinition = serializationContext.getClassDefinitionContainer()
					.getClassDefinitionById(keyClassId);

			ClassDefinition valueClassDefinition = serializationContext.getClassDefinitionContainer()
					.getClassDefinitionById(valueClassId);

			Marshaller keyMarshaller;
			Marshaller valueMarshaller;
			if (mapKeyType != null) {
				ensureMarshallersInitialized(serializationContext);
				keyMarshaller = mapKeyTypeMarshaller;
				valueMarshaller = mapValueTypeMarshaller;
			}
			else {
				keyMarshaller = serializationContext.findMarshaller(keyClassDefinition.getType());
				valueMarshaller = serializationContext.findMarshaller(valueClassDefinition.getType());
			}

			Object key = keyMarshaller.unmarshall(keyClassDefinition.getType(), dataInput, serializationContext);
			Object value = valueMarshaller.unmarshall(valueClassDefinition.getType(), dataInput, serializationContext);
			map.put(key, value);
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
