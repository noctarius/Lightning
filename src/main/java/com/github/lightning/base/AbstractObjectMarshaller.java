package com.github.lightning.base;

import java.io.DataInput;
import java.io.IOException;

import com.github.lightning.ObjectInstantiator;

public abstract class AbstractObjectMarshaller extends AbstractMarshaller {

	@Override
	@SuppressWarnings("unchecked")
	public final <V> V unmarshall(Class<?> type, ObjectInstantiator objectInstantiator, DataInput dataInput) throws IOException {
		V value = (V) objectInstantiator.newInstance();
		return unmarshall(value, type, dataInput);
	}

	@Override
	protected <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
		// Never used in this strategy
		return null;
	}

	protected abstract <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException;
}
