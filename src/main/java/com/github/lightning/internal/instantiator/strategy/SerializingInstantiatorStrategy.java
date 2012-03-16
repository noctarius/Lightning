/**
 * Copyright 2006-2009 the original author or authors.
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
package com.github.lightning.internal.instantiator.strategy;

import java.io.NotSerializableException;
import java.io.Serializable;

import com.github.lightning.internal.instantiator.ObjectInstantiator;
import com.github.lightning.internal.instantiator.ObjenesisException;
import com.github.lightning.internal.instantiator.basic.ObjectStreamClassInstantiator;
import com.github.lightning.internal.instantiator.gcj.GCJSerializationInstantiator;
import com.github.lightning.internal.instantiator.perc.PercSerializationInstantiator;
import com.github.lightning.internal.instantiator.sun.Sun13SerializationInstantiator;
import com.github.lightning.internal.instantiator.sun.SunUnsafeAllocateInstanceInstantiator;

/**
 * Guess the best serializing instantiator for a given class. The returned
 * instantiator will
 * instantiate classes like the genuine java serialization framework (the
 * constructor of the first
 * not serializable class will be called). Currently, the selection doesn't
 * depend on the class. It
 * relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 * 
 * @author Henri Tremblay
 * @see ObjectInstantiator
 */
public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy {

	private static final boolean UNSAFE_AVAILABLE;

	static {
		boolean unsafeAvailable = false;
		try {
			Class.forName("sun.misc.Unsafe");
			unsafeAvailable = true;
		}
		catch (Exception e) {
			// Intentionally left blank
		}

		UNSAFE_AVAILABLE = unsafeAvailable;
	}

	/**
	 * Return an {@link ObjectInstantiator} allowing to create instance
	 * following the java
	 * serialization framework specifications.
	 * 
	 * @param type
	 *            Class to instantiate
	 * @return The ObjectInstantiator for the class
	 */
	@Override
	public ObjectInstantiator newInstantiatorOf(Class type) {
		if (!Serializable.class.isAssignableFrom(type)) {
			throw new ObjenesisException(new NotSerializableException(type + " not serializable"));
		}
		if (JVM_NAME.startsWith(SUN)) {
			if (VM_VERSION.startsWith("1.3")) {
				return new Sun13SerializationInstantiator(type);
			}
			else if (UNSAFE_AVAILABLE) {
				return new SunUnsafeAllocateInstanceInstantiator(type);
			}
		}
		else if (JVM_NAME.startsWith(GNU)) {
			return new GCJSerializationInstantiator(type);
		}
		else if (JVM_NAME.startsWith(PERC)) {
			return new PercSerializationInstantiator(type);
		}

		return new ObjectStreamClassInstantiator(type);
	}

}
