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

import com.carrotsearch.hppc.ObjectObjectMap;
import com.carrotsearch.hppc.ObjectObjectOpenHashMap;
import com.github.lightning.Marshaller;
import com.github.lightning.MarshallerContext;

public class InternalMarshallerContext implements MarshallerContext {

	private final MarshallerContext parentMarshallerContext;
	private final ObjectObjectMap<Class<?>, Marshaller> marshallers = new ObjectObjectOpenHashMap<Class<?>, Marshaller>();

	public InternalMarshallerContext() {
		this(null);
	}

	public InternalMarshallerContext(MarshallerContext parentMarshallerContext) {
		this.parentMarshallerContext = parentMarshallerContext;
	}

	@Override
	public Marshaller getMarshaller(Class<?> type) {
		Marshaller marshaller = marshallers.get(type);
		if (marshaller != null) {
			return marshaller;
		}

		if (parentMarshallerContext != null) {
			return parentMarshallerContext.getMarshaller(type);
		}

		return null;
	}

	@Override
	public void bindMarshaller(Class<?> type, Marshaller marshaller) {
		marshallers.put(type, marshaller);
	}

	public ObjectObjectMap<Class<?>, Marshaller> getInternalMap() {
		return marshallers;
	}
}
