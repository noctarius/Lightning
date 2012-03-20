package com.github.lightning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.github.lightning.WhatShouldItLookLike.DebugLogger;
import com.github.lightning.base.AbstractSerializerDefinition;

public class ClassDefinitionContainerTestCase {

	@Test
	public void testLightningChecksum() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new SerializerDefinition()).build();

		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new SerializerDefinition()).build();

		remoteSerializer.setClassDefinitionContainer(container);
	}

	@Test(expected = ClassDefinitionNotConstistentException.class)
	public void testLightningChecksumFailing() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new SerializerDefinition()).build();

		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.newBuilder().logger(new DebugLogger()).build();

		remoteSerializer.setClassDefinitionContainer(container);
	}

	@Test
	public void testSerialVersionUID() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger())
				.classComparisonStrategy(ClassComparisonStrategy.SerialVersionUID)
				.serializerDefinitions(new SerializerDefinition()).build();

		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new SerializerDefinition()).build();

		remoteSerializer.setClassDefinitionContainer(container);
	}

	@Test(expected = ClassDefinitionNotConstistentException.class)
	public void testSerialVersionUIDFailing() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger())
				.classComparisonStrategy(ClassComparisonStrategy.SerialVersionUID)
				.serializerDefinitions(new SerializerDefinition()).build();

		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.newBuilder().logger(new DebugLogger()).build();

		remoteSerializer.setClassDefinitionContainer(container);
	}

	@Test
	public void testClassDefinitionContainerTransportLightningChecksum() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new SerializerDefinition()).build();

		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);

		out.writeObject(container);
		byte[] data = baos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bais);

		ClassDefinitionContainer remoteContainer = (ClassDefinitionContainer) in.readObject();

		Serializer remoteSerializer = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new SerializerDefinition()).build();

		remoteSerializer.setClassDefinitionContainer(remoteContainer);
	}

	@Test
	public void testClassDefinitionContainerTransportSerialVersionUID() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger())
				.classComparisonStrategy(ClassComparisonStrategy.SerialVersionUID)
				.serializerDefinitions(new SerializerDefinition()).build();

		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);

		out.writeObject(container);
		byte[] data = baos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bais);

		ClassDefinitionContainer remoteContainer = (ClassDefinitionContainer) in.readObject();

		Serializer remoteSerializer = Lightning.newBuilder().logger(new DebugLogger())
				.classComparisonStrategy(ClassComparisonStrategy.SerialVersionUID)
				.serializerDefinitions(new SerializerDefinition()).build();

		remoteSerializer.setClassDefinitionContainer(remoteContainer);
	}

	public static class SerializerDefinition extends AbstractSerializerDefinition {

		@Override
		protected void configure() {
			bind(Foo.class).attributes();
			bind(Bar.class).attributes();
		}
	}

	public static class Foo {

		@Attribute
		private int id;

		@Attribute
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Bar {

		@Attribute
		private int id;

		@Attribute
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
