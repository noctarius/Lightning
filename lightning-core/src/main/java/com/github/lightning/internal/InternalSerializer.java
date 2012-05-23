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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.github.lightning.ClassComparisonStrategy;
import com.github.lightning.Marshaller;
import com.github.lightning.MarshallerStrategy;
import com.github.lightning.SerializationContext;
import com.github.lightning.SerializationStrategy;
import com.github.lightning.exceptions.ClassDefinitionInconsistentException;
import com.github.lightning.exceptions.SerializerExecutionException;
import com.github.lightning.instantiator.ObjectInstantiatorFactory;
import com.github.lightning.internal.generator.BytecodeMarshallerGenerator;
import com.github.lightning.internal.generator.MarshallerGenerator;
import com.github.lightning.internal.io.BufferInputStream;
import com.github.lightning.internal.io.BufferOutputStream;
import com.github.lightning.internal.io.ReaderInputStream;
import com.github.lightning.internal.io.WriterOutputStream;
import com.github.lightning.logging.Logger;
import com.github.lightning.metadata.ClassDefinition;
import com.github.lightning.metadata.ClassDefinitionContainer;
import com.github.lightning.metadata.ClassDescriptor;

class InternalSerializer implements ClassDescriptorAwareSerializer {

	private final AtomicReference<ClassDefinitionContainer> classDefinitionContainer = new AtomicReference<ClassDefinitionContainer>();
	private final MarshallerGenerator marshallerGenerator = new BytecodeMarshallerGenerator();
	private final ObjectInstantiatorFactory objectInstantiatorFactory;
	private final ClassComparisonStrategy classComparisonStrategy;
	private final Map<Class<?>, ClassDescriptor> classDescriptors;
	private final SerializationStrategy serializationStrategy;
	private final Map<Class<?>, Marshaller> definedMarshallers;
	private final MarshallerStrategy marshallerStrategy;

	InternalSerializer(ClassDefinitionContainer classDefinitionContainer, SerializationStrategy serializationStrategy,
			ClassComparisonStrategy classComparisonStrategy, Map<Class<?>, ClassDescriptor> classDescriptors, Map<Class<?>, Marshaller> marshallers,
			ObjectInstantiatorFactory objectInstantiatorFactory, Logger logger, MarshallerStrategy marshallerStrategy, File debugCacheDirectory) {

		this.classDefinitionContainer.set(classDefinitionContainer);
		this.classComparisonStrategy = classComparisonStrategy;
		this.classDescriptors = Collections.unmodifiableMap(classDescriptors);
		this.serializationStrategy = serializationStrategy;

		for (ClassDescriptor classDescriptor : classDescriptors.values()) {
			if (classDescriptor instanceof InternalClassDescriptor && classDescriptor.getMarshaller() == null) {
				Marshaller marshaller = marshallerGenerator.generateMarshaller(classDescriptor.getType(), classDescriptor.getPropertyDescriptors(),
						marshallers, this, serializationStrategy, objectInstantiatorFactory, debugCacheDirectory);

				((InternalClassDescriptor) classDescriptor).setMarshaller(marshaller);
				marshallers.put(classDescriptor.getType(), marshaller);
			}
		}

		this.definedMarshallers = marshallers;
		this.marshallerStrategy = marshallerStrategy;
		this.objectInstantiatorFactory = objectInstantiatorFactory;
	}

	@Override
	public ClassDefinitionContainer getClassDefinitionContainer() {
		return classDefinitionContainer.get();
	}

	@Override
	public void setClassDefinitionContainer(ClassDefinitionContainer classDefinitionContainer) {
		// Pre-check if checksums of remote classes passing
		ClassDefinitionContainer oldClassDefinitionContainer = getClassDefinitionContainer();
		consistencyCheckClassChecksums(oldClassDefinitionContainer, classDefinitionContainer);

		// Set new ClassDefinitionContainer if checking succeed
		this.classDefinitionContainer.set(classDefinitionContainer);
	}

	@Override
	public <V> void serialize(V value, DataOutput dataOutput) {
		try {
			SerializationContext serializationContext = new InternalSerializationContext(classDefinitionContainer.get(), serializationStrategy,
					marshallerStrategy, objectInstantiatorFactory, definedMarshallers);

			Class<?> type = value.getClass();
			ClassDescriptor classDescriptor = findClassDescriptor(type);
			dataOutput.writeLong(classDescriptor.getClassDefinition().getId());
			classDescriptor.getMarshaller().marshall(value, type, dataOutput, serializationContext);
		}
		catch (IOException e) {
			throw new SerializerExecutionException("Error while serializing value", e);
		}
	}

	@Override
	public <V> void serialize(V value, OutputStream outputStream) {
		if (outputStream instanceof DataOutput)
			serialize(value, (DataOutput) outputStream);
		else
			serialize(value, (DataOutput) new DataOutputStream(outputStream));
	}

	@Override
	public <V> void serialize(V value, Writer writer) {
		serialize(value, (DataOutput) new DataOutputStream(new WriterOutputStream(writer, "UTF-8")));
	}

	@Override
	public <V> void serialize(V value, ByteBuffer buffer) {
		serialize(value, (DataOutput) new DataOutputStream(new BufferOutputStream(buffer)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V deserialize(DataInput dataInput) {
		try {
			SerializationContext serializationContext = new InternalSerializationContext(classDefinitionContainer.get(), serializationStrategy,
					marshallerStrategy, objectInstantiatorFactory, definedMarshallers);

			long typeId = dataInput.readLong();
			Class<?> clazz = classDefinitionContainer.get().getTypeById(typeId);
			ClassDescriptor classDescriptor = findClassDescriptor(clazz);
			return (V) classDescriptor.getMarshaller().unmarshall(clazz, dataInput, serializationContext);
		}
		catch (IOException e) {
			throw new SerializerExecutionException("Error while deserializing value", e);
		}
	}

	@Override
	public <V> V deserialize(InputStream inputStream) {
		if (inputStream instanceof DataInput) {
			return deserialize((DataInput) inputStream);
		}

		return deserialize((DataInput) new DataInputStream(inputStream));
	}

	@Override
	public <V> V deserialize(Reader reader) {
		return deserialize((DataInput) new DataInputStream(new ReaderInputStream(reader, "UTF-8")));
	}

	@Override
	public <V> V deserialize(ByteBuffer buffer) {
		return deserialize((DataInput) new DataInputStream(new BufferInputStream(buffer)));
	}

	@Override
	public ClassDescriptor findClassDescriptor(Class<?> type) {
		return classDescriptors.get(type);
	}

	private void consistencyCheckClassChecksums(ClassDefinitionContainer oldClassDefinitionContainer, ClassDefinitionContainer classDefinitionContainer) {
		for (ClassDefinition classDefinition : classDefinitionContainer.getClassDefinitions()) {
			ClassDefinition oldClassDefinition = oldClassDefinitionContainer.getClassDefinitionByCanonicalName(classDefinition.getCanonicalName());
			if (oldClassDefinition == null) {
				throw new ClassDefinitionInconsistentException("No ClassDefinition for type " + classDefinition.getCanonicalName() + " was found");
			}

			if (classComparisonStrategy == ClassComparisonStrategy.SerialVersionUID) {
				long serialVersionUID = classDefinition.getSerialVersionUID();
				long oldSerialVersionUID = oldClassDefinition.getSerialVersionUID();
				if (serialVersionUID != oldSerialVersionUID) {
					throw new ClassDefinitionInconsistentException("SerialVersionUID of type " + classDefinition.getCanonicalName() + " is not constistent");
				}
			}
			else {
				byte[] checksum = classDefinition.getChecksum();
				byte[] oldChecksum = oldClassDefinition.getChecksum();
				if (!Arrays.equals(checksum, oldChecksum)) {
					throw new ClassDefinitionInconsistentException("Signature checksum of type " + classDefinition.getCanonicalName() + " is not constistent");
				}
			}
		}
	}
}
