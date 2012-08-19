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
package com.github.lightning;

import java.lang.reflect.Type;

import com.github.lightning.instantiator.ObjectInstantiatorFactory;
import com.github.lightning.metadata.ClassDefinitionContainer;
import com.github.lightning.metadata.ValueNullableEvaluator;

public interface SerializationContext {

	ClassDefinitionContainer getClassDefinitionContainer();

	SerializationStrategy getSerializationStrategy();

	ObjectInstantiatorFactory getObjectInstantiatorFactory();

	Marshaller findMarshaller(Type type);

	ValueNullableEvaluator getValueNullableEvaluator();

	long findReferenceIdByObject(Object instance);

	Object findObjectByReferenceId(long referenceId);

	boolean containsReferenceId(long referenceId);

	long putMarshalledInstance(Object instance);

	long putUnmarshalledInstance(long refrenceId, Object instance);

}
