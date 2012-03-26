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
package com.github.lightning.internal;

import java.util.HashMap;
import java.util.Map;

import com.github.lightning.SerializationContext;
import com.github.lightning.SerializationStrategy;
import com.github.lightning.internal.bundle.cern.colt.map.AbstractLongObjectMap;
import com.github.lightning.internal.bundle.cern.colt.map.OpenLongObjectHashMap;
import com.github.lightning.metadata.ClassDefinitionContainer;

public class InternalSerializationContext implements SerializationContext {

	private final Map<Object, Long> referencesMarshall = new HashMap<Object, Long>();
	private final AbstractLongObjectMap<Object> referencesUnmarshall = new OpenLongObjectHashMap<Object>(Object.class);

	private final ClassDefinitionContainer classDefinitionContainer;
	private final SerializationStrategy serializationStrategy;

	private long nextReferenceIdMarshall = 10000;

	public InternalSerializationContext(ClassDefinitionContainer classDefinitionContainer,
			SerializationStrategy serializationStrategy) {

		this.classDefinitionContainer = classDefinitionContainer;
		this.serializationStrategy = serializationStrategy;
	}

	@Override
	public ClassDefinitionContainer getClassDefinitionContainer() {
		return classDefinitionContainer;
	}

	@Override
	public SerializationStrategy getSerializationStrategy() {
		return serializationStrategy;
	}

	@Override
	public long findReferenceIdByObject(Object instance) {
		Long referenceId = referencesMarshall.get(instance);
		if (referenceId == null) {
			return -1;
		}
		return referenceId;
	}

	@Override
	public long putMarshalledInstance(Object instance) {
		long newId = getNextReferenceIdMarshall();
		referencesMarshall.put(instance, newId);
		return newId;
	}

	@Override
	public Object findObjectByReferenceId(long referenceId) {
		return referencesUnmarshall.get(referenceId);
	}

	@Override
	public boolean containsReferenceId(long referenceId) {
		return referencesUnmarshall.containsKey(referenceId);
	}

	@Override
	public long putUnmarshalledInstance(long refrenceId, Object instance) {
		referencesUnmarshall.put(refrenceId, instance);
		return refrenceId;
	}

	public Map<Object, Long> getReferencesMarshall() {
		return referencesMarshall;
	}

	public AbstractLongObjectMap<Object> getReferencesUnmarshall() {
		return referencesUnmarshall;
	}

	public long getNextReferenceIdMarshall() {
		long newId = nextReferenceIdMarshall++;
		return newId;
	}
}
