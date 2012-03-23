package com.github.lightning.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.Marshaller;
import com.github.lightning.internal.instantiator.ObjenesisSerializer;


class ObjenesisDelegatingMarshaller implements Marshaller {

	private final ObjenesisSerializer objenesisSerializer;
	private final AbstractObjectMarshaller delegatedMarshaller;
	
	ObjenesisDelegatingMarshaller(AbstractObjectMarshaller delegatedMarshaller, ObjenesisSerializer objenesisSerializer) {
		this.delegatedMarshaller = delegatedMarshaller;
		this.objenesisSerializer = objenesisSerializer;
	}
	
	@Override
	public boolean acceptType(Class<?> type) {
		return delegatedMarshaller.acceptType(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput, ClassDefinitionContainer classDefinitionContainer) throws IOException {
		delegatedMarshaller.marshall(value, type, dataOutput, classDefinitionContainer);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(Class<?> type, DataInput dataInput, ClassDefinitionContainer classDefinitionContainer) throws IOException {
		V value = (V) objenesisSerializer.newInstance(type);
		return delegatedMarshaller.unmarshall(value, type, dataInput, classDefinitionContainer);
	}

}
