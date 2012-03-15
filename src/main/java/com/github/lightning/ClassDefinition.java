package com.github.lightning;

public interface ClassDefinition {

	String getCanonicalName();

	Class<?> getType();

	byte[] getChecksum();

	long getId();

}
