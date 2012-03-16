package com.github.lightning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;

public interface Serializer {

	ClassDefinitionContainer getClassDefinitionContainer();

	void setClassDefinitionContainer(ClassDefinitionContainer classDefinitionContainer);

	<V> void serialize(V value, DataOutput dataOutput);

	<V> void serialize(V value, OutputStream outputStream);

	<V> void serialize(V value, Writer writer);

	<V> void serialize(V value, ByteBuffer buffer);

	<V> V deserialize(DataInput dataInput);

	<V> V deserialize(InputStream inputStream);

	<V> V deserialize(Reader reader);

	<V> V deserialize(ByteBuffer buffer);

}
