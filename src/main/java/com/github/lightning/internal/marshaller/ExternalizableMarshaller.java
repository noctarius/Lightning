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
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.github.lightning.base.AbstractObjectMarshaller;

public class ExternalizableMarshaller extends AbstractObjectMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return Externalizable.class.isAssignableFrom(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		((Externalizable) value).writeExternal((ObjectOutput) dataOutput);
	}

	@Override
	public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
		try {
			((Externalizable) value).readExternal((ObjectInput) dataInput);
			return value;
		}
		catch (ClassNotFoundException e) {
			throw new IOException("Error while deserialization", e);
		}
	}
}
