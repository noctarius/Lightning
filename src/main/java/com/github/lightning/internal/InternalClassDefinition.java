package com.github.lightning.internal;

import java.util.Arrays;

import com.github.lightning.ClassDefinition;
import com.github.lightning.internal.util.ClassUtil;
import com.github.lightning.internal.util.Crc64Util;
import com.github.lightning.internal.util.InternalUtil;

class InternalClassDefinition implements ClassDefinition {

	private final String canonicalName;
	private final Class<?> type;
	private final byte[] checksum;
	private long id;

	InternalClassDefinition(Class<?> type) {
		this.canonicalName = type.getCanonicalName();
		this.type = type;

		byte[] classData = ClassUtil.getClassBytes(type);
		this.checksum = InternalUtil.getChecksum(classData);
		this.id = Crc64Util.checksum(classData);
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
}
