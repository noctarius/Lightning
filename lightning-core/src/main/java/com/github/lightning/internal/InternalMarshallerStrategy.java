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
package com.github.lightning.internal;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.lightning.Marshaller;
import com.github.lightning.MarshallerContext;
import com.github.lightning.MarshallerStrategy;
import com.github.lightning.Streamed;
import com.github.lightning.internal.marshaller.BigDecimalMarshaller;
import com.github.lightning.internal.marshaller.BigIntegerMarshaller;
import com.github.lightning.internal.marshaller.BooleanArrayMarshaller;
import com.github.lightning.internal.marshaller.BooleanMarshaller;
import com.github.lightning.internal.marshaller.ByteArrayMarshaller;
import com.github.lightning.internal.marshaller.ByteMarshaller;
import com.github.lightning.internal.marshaller.CharacterArrayMarshaller;
import com.github.lightning.internal.marshaller.CharacterMarshaller;
import com.github.lightning.internal.marshaller.DoubleArrayMarshaller;
import com.github.lightning.internal.marshaller.DoubleMarshaller;
import com.github.lightning.internal.marshaller.EnumMarshaller;
import com.github.lightning.internal.marshaller.ExternalizableMarshaller;
import com.github.lightning.internal.marshaller.FloatArrayMarshaller;
import com.github.lightning.internal.marshaller.FloatMarshaller;
import com.github.lightning.internal.marshaller.IntegerArrayMarshaller;
import com.github.lightning.internal.marshaller.IntegerMarshaller;
import com.github.lightning.internal.marshaller.ListMarshaller;
import com.github.lightning.internal.marshaller.LongArrayMarshaller;
import com.github.lightning.internal.marshaller.LongMarshaller;
import com.github.lightning.internal.marshaller.MapMarshaller;
import com.github.lightning.internal.marshaller.SerializableMarshaller;
import com.github.lightning.internal.marshaller.SetMarshaller;
import com.github.lightning.internal.marshaller.ShortArrayMarshaller;
import com.github.lightning.internal.marshaller.ShortMarshaller;
import com.github.lightning.internal.marshaller.StreamedMarshaller;
import com.github.lightning.internal.marshaller.StringMarshaller;
import com.github.lightning.internal.util.TypeUtil;

public class InternalMarshallerStrategy implements MarshallerStrategy {

	public static final List<Marshaller> baseMarshaller;

	static {
		List<Marshaller> marshallers = new ArrayList<Marshaller>();
		marshallers.add(new StreamedMarshaller());
		marshallers.add(new ExternalizableMarshaller());
		marshallers.add(new BooleanMarshaller());
		marshallers.add(new ByteMarshaller());
		marshallers.add(new CharacterMarshaller());
		marshallers.add(new ShortMarshaller());
		marshallers.add(new IntegerMarshaller());
		marshallers.add(new LongMarshaller());
		marshallers.add(new FloatMarshaller());
		marshallers.add(new DoubleMarshaller());
		marshallers.add(new StringMarshaller());
		marshallers.add(new EnumMarshaller());
		marshallers.add(new ListMarshaller());
		marshallers.add(new SetMarshaller());
		marshallers.add(new MapMarshaller());
		marshallers.add(new BigIntegerMarshaller());
		marshallers.add(new BigDecimalMarshaller());
		marshallers.add(new BooleanArrayMarshaller());
		marshallers.add(new ByteArrayMarshaller());
		marshallers.add(new CharacterArrayMarshaller());
		marshallers.add(new ShortArrayMarshaller());
		marshallers.add(new IntegerArrayMarshaller());
		marshallers.add(new LongArrayMarshaller());
		marshallers.add(new FloatArrayMarshaller());
		marshallers.add(new DoubleArrayMarshaller());

		baseMarshaller = Collections.unmodifiableList(marshallers);
	}

	private final Marshaller externalizableMarshaller = new ExternalizableMarshaller();
	private final Marshaller serializableMarshaller = new SerializableMarshaller();
	private final Marshaller streamedMarshaller = new StreamedMarshaller();

	@Override
	public Marshaller getMarshaller(Type type, MarshallerContext marshallerContext) {
		return getMarshaller(type, marshallerContext, false);
	}

	@Override
	public Marshaller getMarshaller(Type type, MarshallerContext marshallerContext, boolean baseMarshallersOnly) {
		Class<?> rawType = TypeUtil.getBaseType(type);
		if (Streamed.class.isAssignableFrom(rawType)) {
			return streamedMarshaller;
		}

		if (!baseMarshallersOnly && Externalizable.class.isAssignableFrom(rawType)) {
			return externalizableMarshaller;
		}

		if (marshallerContext != null) {
			Marshaller marshaller = marshallerContext.getMarshaller(type);
			if (marshaller != null) {
				return marshaller;
			}
		}

		for (Marshaller temp : baseMarshaller) {
			if (temp.acceptType(rawType)) {
				return temp;
			}
		}

		if (!baseMarshallersOnly && Serializable.class.isAssignableFrom(rawType) && !rawType.isArray()) {
			return serializableMarshaller;
		}

		return null;
	}
}
