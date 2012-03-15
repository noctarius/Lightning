package com.github.lightning.internal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.Buffer;
import java.util.concurrent.atomic.AtomicReference;

import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.Serializer;


class InternalSerializer implements Serializer {

	private final AtomicReference<ClassDefinitionContainer> classDefinitionContainer = new AtomicReference<ClassDefinitionContainer>();
	
	InternalSerializer(ClassDefinitionContainer classDefinitionContainer) {
		
	}
	
	@Override
	public ClassDefinitionContainer getClassDefinitionContainer() {
		return classDefinitionContainer.get();
	}

	@Override
	public void setClassDefinitionContainer(ClassDefinitionContainer classDefinitionContainer) {
		// precheck if checksums of remote classes passing 

	}

	@Override
	public <V> void serialize(V value, DataOutput dataOutput) {
		// TODO Auto-generated method stub

	}

	@Override
	public <V> void serialize(V value, OutputStream outputStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public <V> void serialize(V value, Writer writer) {
		// TODO Auto-generated method stub

	}

	@Override
	public <V> void serialize(V value, Buffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public <V> V deserialize(DataInput dataInput) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> V deserialize(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> V deserialize(Reader reader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> V deserialize(Buffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

}
