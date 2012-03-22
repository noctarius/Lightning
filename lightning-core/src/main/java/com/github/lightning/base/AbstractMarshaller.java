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
import java.io.DataOutput;
import java.io.IOException;

import com.github.lightning.Marshaller;

public abstract class AbstractMarshaller implements Marshaller {

	@Override
	public <V> V unmarshall(Class<?> type, DataInput dataInput) throws IOException {
		return unmarshall(type, dataInput);
	}

	protected boolean writePossibleNull(Object value, DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(value == null ? 1 : 0);
		return value != null;
	}

	protected boolean isNull(DataInput dataInput) throws IOException {
		byte isNull = dataInput.readByte();
		return isNull == 1 ? true : false;
	}
}
