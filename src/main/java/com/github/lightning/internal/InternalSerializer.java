package com.github.lightning.internal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import com.github.lightning.ClassDefinition;
import com.github.lightning.ClassDefinitionContainer;
import com.github.lightning.ClassDefinitionNotConstistentException;
import com.github.lightning.Serializer;

class InternalSerializer implements Serializer {

	private final AtomicReference<ClassDefinitionContainer> classDefinitionContainer = new AtomicReference<ClassDefinitionContainer>();

	InternalSerializer(ClassDefinitionContainer classDefinitionContainer) {
		this.classDefinitionContainer.set(classDefinitionContainer);
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

	private void consistencyCheckClassChecksums(ClassDefinitionContainer oldClassDefinitionContainer, ClassDefinitionContainer classDefinitionContainer) {
		for (ClassDefinition classDefinition : classDefinitionContainer.getClassDefinitions()) {
			ClassDefinition oldClassDefinition = oldClassDefinitionContainer.getClassDefinitionByCanonicalName(classDefinition.getCanonicalName());
			if (oldClassDefinition == null) {
				throw new ClassDefinitionNotConstistentException("No classDefinition for class " + classDefinition.getCanonicalName() + " was found");
			}

			byte[] checksum = classDefinition.getChecksum();
			byte[] oldChecksum = oldClassDefinition.getChecksum();
			if (!Arrays.equals(checksum, oldChecksum)) {
				throw new ClassDefinitionNotConstistentException("Signature checksum of class " + classDefinition.getCanonicalName() + " is not constistent");
			}
		}
	}
}
