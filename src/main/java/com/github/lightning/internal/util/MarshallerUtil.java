package com.github.lightning.internal.util;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.lightning.Marshaller;
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

public class MarshallerUtil {

	private static final Marshaller EXTERNALIZABLE_MARSHALLER = new ExternalizableMarshaller();
	private static final Marshaller SERIALIZABLE_MARSHALLER = new SerializableMarshaller();
	private static final Marshaller STREAMED_MARSHALLER = new StreamedMarshaller();

	public static final List<Marshaller> BASE_MARSHALLER;

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

		BASE_MARSHALLER = Collections.unmodifiableList(marshallers);
	}

	private MarshallerUtil() {
	}

	public static Marshaller getBestMatchingMarshaller(Class<?> type, Map<Class<?>, Marshaller> marshallers) {
		if (Streamed.class.isAssignableFrom(type)) {
			return STREAMED_MARSHALLER;
		}

		if (Externalizable.class.isAssignableFrom(type)) {
			return EXTERNALIZABLE_MARSHALLER;
		}

		Marshaller marshaller = marshallers.get(type);
		if (marshaller != null) {
			return marshaller;
		}

		for (Marshaller temp : BASE_MARSHALLER) {
			if (temp.acceptType(type)) {
				return temp;
			}
		}

		return SERIALIZABLE_MARSHALLER;
	}
}
