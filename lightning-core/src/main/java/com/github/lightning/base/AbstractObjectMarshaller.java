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
