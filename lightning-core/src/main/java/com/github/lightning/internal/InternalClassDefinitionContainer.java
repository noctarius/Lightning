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
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.Streamed;
import com.github.lightning.internal.bundle.cern.colt.map.AbstractLongObjectMap;
import com.github.lightning.internal.bundle.cern.colt.map.OpenLongObjectHashMap;
import com.github.lightning.internal.util.ClassUtil;

class InternalClassDefinitionContainer implements ClassDefinitionContainer, Streamed, Externalizable {

	private final List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
	private final AbstractLongObjectMap<ClassDefinition> classDefinitionsMappings;

	// Serialization
	public InternalClassDefinitionContainer() {
		classDefinitionsMappings = new OpenLongObjectHashMap<ClassDefinition>(ClassDefinition.class);
	}

	InternalClassDefinitionContainer(List<ClassDefinition> classDefinitions) {
		this.classDefinitions.addAll(classDefinitions);
		classDefinitionsMappings = new OpenLongObjectHashMap<ClassDefinition>(ClassDefinition.class, classDefinitions.size());
		initMappings(classDefinitions);
	}

	@Override
	public Collection<ClassDefinition> getClassDefinitions() {
		return Collections.unmodifiableCollection(classDefinitions);
	}

	@Override
	public Class<?> getTypeById(long id) {
		ClassDefinition classDefinition = classDefinitionsMappings.get(id);
		return classDefinition != null ? classDefinition.getType() : null;
	}

	@Override
	public ClassDefinition getClassDefinitionByCanonicalName(String canonicalName) {
		for (ClassDefinition classDefinition : classDefinitions) {
			if (classDefinition.getCanonicalName().equals(canonicalName)) {
				return classDefinition;
			}
		}
		return null;
	}

	@Override
	public ClassDefinition getClassDefinitionById(long id) {
		ClassDefinition classDefinition = classDefinitionsMappings.get(id);
		return classDefinition != null ? classDefinition : null;
	}

	@Override
	public ClassDefinition getClassDefinitionByType(Class<?> type) {
		for (ClassDefinition classDefinition : classDefinitions) {
			if (classDefinition.getType() == type) {
				return classDefinition;
			}
		}
		return null;
	}

	@Override
	public void writeTo(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(classDefinitions.size());
		for (ClassDefinition classDefinition : classDefinitions) {
			final long id = classDefinition.getId();
			final byte[] checksum = classDefinition.getChecksum();
			final String canonicalName = classDefinition.getCanonicalName();
			final long serialVersionUID = classDefinition.getSerialVersionUID();

			dataOutput.writeLong(id);
			dataOutput.writeUTF(canonicalName);
			dataOutput.write(checksum);
			dataOutput.writeLong(serialVersionUID);
		}
	}

	@Override
	public void readFrom(DataInput dataInput) throws IOException {
		int size = dataInput.readInt();
		for (int i = 0; i < size; i++) {
			final long id = dataInput.readLong();
			final String canonicalName = dataInput.readUTF();
			final byte[] checksum = new byte[20];
			dataInput.readFully(checksum);
			final long serialVersionUID = dataInput.readLong();

			try {
				Class<?> type = ClassUtil.loadClass(canonicalName);
				classDefinitions.add(new InternalClassDefinition(id, type, checksum, serialVersionUID));
			}
			catch (ClassNotFoundException e) {
				throw new IOException("Class " + canonicalName + " could not be loaded", e);
			}
		}

		initMappings(classDefinitions);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		writeTo(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		readFrom(in);
	}

	private void initMappings(List<ClassDefinition> classDefinitions) {
		for (ClassDefinition classDefinition : classDefinitions) {
			classDefinitionsMappings.put(classDefinition.getId(), classDefinition);
		}
	}
}
