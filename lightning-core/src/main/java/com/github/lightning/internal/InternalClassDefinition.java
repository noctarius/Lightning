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

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.Type;

import com.github.lightning.ClassDefinition;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.util.ClassUtil;
import com.github.lightning.internal.util.Crc64Util;
import com.github.lightning.internal.util.InternalUtil;
import com.github.lightning.logging.Logger;

class InternalClassDefinition implements ClassDefinition, Comparable<ClassDefinition> {

	private final String canonicalName;
	private final Class<?> type;
	private final byte[] checksum;
	private final long serialVersionUID;

	private long id;

	InternalClassDefinition(Class<?> type, List<PropertyDescriptor> propertyDescriptors, Logger logger) {
		this.canonicalName = Type.getInternalName(type).replace("/", ".");
		this.type = type;

		byte[] classData = ClassUtil.getClassBytes(type);
		this.checksum = InternalUtil.getChecksum(propertyDescriptors, logger);
		this.id = Crc64Util.checksum(classData);
		this.serialVersionUID = ClassUtil.calculateSerialVersionUID(type);
	}

	InternalClassDefinition(long id, Class<?> type, byte[] checksum, long serialVersionUID) {
		this.canonicalName = Type.getInternalName(type).replace("/", ".");
		this.type = type;
		this.id = id;
		this.checksum = checksum;
		this.serialVersionUID = serialVersionUID;
	}

	@Override
	public String getCanonicalName() {
		return canonicalName;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public byte[] getChecksum() {
		return Arrays.copyOf(checksum, checksum.length);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public int compareTo(ClassDefinition o) {
		return canonicalName.compareTo(o.getCanonicalName());
	}
}
