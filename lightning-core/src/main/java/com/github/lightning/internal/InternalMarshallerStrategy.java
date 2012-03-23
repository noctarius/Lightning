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

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.lightning.Marshaller;
import com.github.lightning.MarshallerStrategy;
import com.github.lightning.Streamed;
import com.github.lightning.internal.marshaller.BooleanMarshaller;
import com.github.lightning.internal.marshaller.ByteMarshaller;
import com.github.lightning.internal.marshaller.CharacterMarshaller;
import com.github.lightning.internal.marshaller.DoubleMarshaller;
import com.github.lightning.internal.marshaller.ExternalizableMarshaller;
import com.github.lightning.internal.marshaller.FloatMarshaller;
import com.github.lightning.internal.marshaller.IntegerMarshaller;
import com.github.lightning.internal.marshaller.LongMarshaller;
import com.github.lightning.internal.marshaller.SerializableMarshaller;
import com.github.lightning.internal.marshaller.ShortMarshaller;
import com.github.lightning.internal.marshaller.StreamedMarshaller;
import com.github.lightning.internal.marshaller.StringMarshaller;

public class InternalMarshallerStrategy implements MarshallerStrategy {

	private final Marshaller externalizableMarshaller = new ExternalizableMarshaller();
	private final Marshaller serializableMarshaller = new SerializableMarshaller();
	private final Marshaller streamedMarshaller = new StreamedMarshaller();

	public final List<Marshaller> baseMarshaller;

	InternalMarshallerStrategy() {
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

		baseMarshaller = Collections.unmodifiableList(marshallers);
	}

	public Marshaller getMarshaller(Class<?> type, Map<Class<?>, Marshaller> definedMarshallers) {
		if (Streamed.class.isAssignableFrom(type)) {
			return streamedMarshaller;
		}

		if (Externalizable.class.isAssignableFrom(type)) {
			return externalizableMarshaller;
		}

		Marshaller marshaller = definedMarshallers.get(type);
		if (marshaller != null) {
			return marshaller;
		}

		for (Marshaller temp : baseMarshaller) {
			if (temp.acceptType(type)) {
				return temp;
			}
		}

		return serializableMarshaller;
	}

}
