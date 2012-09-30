/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import com.github.lightning.SerializationContext;
import com.github.lightning.base.AbstractMarshaller;
import com.github.lightning.metadata.PropertyDescriptor;

public class BigDecimalMarshaller extends AbstractMarshaller {

	private static final Charset CHARSET = Charset.forName("ASCII");

	@Override
	public boolean acceptType(Class<?> type) {
		return BigDecimal.class == type;
	}

	@Override
	public void marshall(Object value, PropertyDescriptor propertyDescriptor, DataOutput dataOutput, SerializationContext serializationContext)
			throws IOException {

		if (!writePossibleNull(value, dataOutput)) {
			return;
		}

		String representation = ((BigDecimal) value).toString();
		byte[] data = representation.getBytes(CHARSET);
		dataOutput.writeInt(data.length);
		dataOutput.write(data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V unmarshall(PropertyDescriptor propertyDescriptor, DataInput dataInput, SerializationContext serializationContext) throws IOException {
		if (isNull(dataInput)) {
			return null;
		}

		int length = dataInput.readInt();
		byte[] data = new byte[length];
		dataInput.readFully(data);

		return (V) new BigDecimal(new String(data, CHARSET));
	}
}
