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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import com.github.lightning.ClassComparisonStrategy;
import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.ClassDefinitionNotConstistentException;
import com.github.lightning.Serializer;
import com.github.lightning.internal.io.BufferInputStream;
import com.github.lightning.internal.io.BufferOutputStream;
import com.github.lightning.internal.io.ReaderInputStream;
import com.github.lightning.internal.io.WriterOutputStream;
import com.github.lightning.logging.Logger;

class InternalSerializer implements Serializer {

	private final AtomicReference<ClassDefinitionContainer> classDefinitionContainer = new AtomicReference<ClassDefinitionContainer>();
	private final ClassComparisonStrategy classComparisonStrategy;

	InternalSerializer(ClassDefinitionContainer classDefinitionContainer, ClassComparisonStrategy classComparisonStrategy, Logger logger) {
		this.classDefinitionContainer.set(classDefinitionContainer);
		this.classComparisonStrategy = classComparisonStrategy;
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
		// TODO Auto-generated method stub

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
	public <V> V deserialize(DataInput dataInput) {
		// TODO Auto-generated method stub
		return null;
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

	private void consistencyCheckClassChecksums(ClassDefinitionContainer oldClassDefinitionContainer, ClassDefinitionContainer classDefinitionContainer) {
		for (ClassDefinition classDefinition : classDefinitionContainer.getClassDefinitions()) {
			ClassDefinition oldClassDefinition = oldClassDefinitionContainer.getClassDefinitionByCanonicalName(classDefinition.getCanonicalName());
			if (oldClassDefinition == null) {
				throw new ClassDefinitionNotConstistentException("No classDefinition for class " + classDefinition.getCanonicalName() + " was found");
			}

			if (classComparisonStrategy == ClassComparisonStrategy.SerialVersionUID) {
				long serialVersionUID = classDefinition.getSerialVersionUID();
				long oldSerialVersionUID = oldClassDefinition.getSerialVersionUID();
				if (serialVersionUID != oldSerialVersionUID) {
					throw new ClassDefinitionNotConstistentException("SerialVersionUID of class " + classDefinition.getCanonicalName() + " is not constistent");
				}
			}
			else {
				byte[] checksum = classDefinition.getChecksum();
				byte[] oldChecksum = oldClassDefinition.getChecksum();
				if (!Arrays.equals(checksum, oldChecksum)) {
					throw new ClassDefinitionNotConstistentException("Signature checksum of class " + classDefinition.getCanonicalName() + " is not constistent");
				}
			}
		}
	}
}
