package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.lightning.Marshaller;
import com.github.lightning.SerializationContext;
import com.github.lightning.TypeBindableMarshaller;
import com.github.lightning.base.AbstractMarshaller;
import com.github.lightning.metadata.ClassDefinition;

public class ListMarshaller extends AbstractMarshaller implements TypeBindableMarshaller {

	private final Class<?> listType;

	private Marshaller listTypeMarshaller;

	public ListMarshaller() {
		this(null);
	}

	private ListMarshaller(Class<?> listType) {
		this.listType = listType;
	}

	@Override
	public boolean acceptType(Class<?> type) {
		return List.class.isAssignableFrom(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput, SerializationContext serializationContext) throws IOException {
		writePossibleNull(value, dataOutput);

		List<?> list = (List<?>) value;
		dataOutput.writeInt(list.size());
		for (Object entry : list) {
			Marshaller marshaller;
			if (listType != null) {
				ensureMarshallerInitialized(serializationContext);
				marshaller = listTypeMarshaller;
			}
			else {
				marshaller = serializationContext.findMarshaller(entry.getClass());
			}

			ClassDefinition classDefinition = serializationContext.getClassDefinitionContainer()
					.getClassDefinitionByType(entry.getClass());

			dataOutput.writeLong(classDefinition.getId());
			marshaller.marshall(entry, entry.getClass(), dataOutput, serializationContext);
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <V> V unmarshall(Class<?> type, DataInput dataInput, SerializationContext serializationContext) throws IOException {
		if (isNull(dataInput)) {
			return null;
		}

		int size = dataInput.readInt();
		List list = new ArrayList(size);
		if (size > 0) {
			long classId = dataInput.readLong();
			ClassDefinition classDefinition = serializationContext.getClassDefinitionContainer()
					.getClassDefinitionById(classId);

			Marshaller marshaller;
			if (listType != null) {
				ensureMarshallerInitialized(serializationContext);
				marshaller = listTypeMarshaller;
			}
			else {
				marshaller = serializationContext.findMarshaller(classDefinition.getType());
			}

			list.add(marshaller.unmarshall(classDefinition.getType(), dataInput, serializationContext));
		}

		return (V) list;
	}

	@Override
	public Marshaller bindType(Field property) {
		Type genericType = property.getGenericType();
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;
			Type[] types = type.getActualTypeArguments();
			if (types.length == 1) {
				Class<?> listType = (Class<?>) types[0];
				return new ListMarshaller(listType);
			}
		}

		return new ListMarshaller();
	}

	private void ensureMarshallerInitialized(SerializationContext serializationContext) {
		if (listTypeMarshaller != null)
			return;

		listTypeMarshaller = serializationContext.findMarshaller(listType);
	}
}
