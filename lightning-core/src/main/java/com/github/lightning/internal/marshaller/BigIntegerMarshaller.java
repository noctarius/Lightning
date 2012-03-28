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
import java.math.BigInteger;

import com.github.lightning.SerializationContext;
import com.github.lightning.base.AbstractMarshaller;

public class BigIntegerMarshaller extends AbstractMarshaller {

	@Override
	public boolean acceptType(Class<?> type) {
		return BigInteger.class == type;
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput, SerializationContext serializationContext) throws IOException {
		if (!writePossibleNull(value, dataOutput)) {
			return;
		}

		byte[] data = ((BigInteger) value).toByteArray();
		dataOutput.writeInt(data.length);
		dataOutput.write(data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(Class<?> type, DataInput dataInput, SerializationContext serializationContext) throws IOException {
		if (isNull(dataInput)) {
			return null;
		}

		int length = dataInput.readInt();
		byte[] data = new byte[length];
		dataInput.readFully(data);

		return (V) new BigInteger(data);
	}
}
