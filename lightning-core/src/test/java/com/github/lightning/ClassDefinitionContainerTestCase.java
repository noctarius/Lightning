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
package com.github.lightning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.exceptions.ClassDefinitionNotConsistentException;
import com.github.lightning.logging.LogLevel;
import com.github.lightning.logging.LoggerAdapter;
import com.github.lightning.metadata.Attribute;
import com.github.lightning.metadata.ClassDefinitionContainer;

public class ClassDefinitionContainerTestCase {

	@Test
	public void testLightningChecksum() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger())
				.debugCacheDirectory(new File("target"))
				.serializerDefinitions(new SerializerDefinition()).build();

		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new SerializerDefinition()).build();

		remoteSerializer.setClassDefinitionContainer(container);
	}

	@Test(expected = ClassDefinitionNotConsistentException.class)
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

	@Test(expected = ClassDefinitionNotConsistentException.class)
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

	public static class DebugLogger extends LoggerAdapter {

		@Override
		public boolean isLogLevelEnabled(LogLevel logLevel) {
			return true;
		}

		@Override
		public boolean isTraceEnabled() {
			return true;
		}

		@Override
		public boolean isDebugEnabled() {
			return true;
		}

		@Override
		public boolean isInfoEnabled() {
			return true;
		}

		@Override
		public boolean isWarnEnabled() {
			return true;
		}

		@Override
		public boolean isErrorEnabled() {
			return true;
		}

		@Override
		public boolean isFatalEnabled() {
			return true;
		}

		@Override
		public void trace(String message) {
			log(LogLevel.Trace, message, null);
		}

		@Override
		public void trace(String message, Throwable throwable) {
			log(LogLevel.Trace, message, throwable);
		}

		@Override
		public void debug(String message) {
			log(LogLevel.Debug, message, null);
		}

		@Override
		public void debug(String message, Throwable throwable) {
			log(LogLevel.Debug, message, throwable);
		}

		@Override
		public void info(String message) {
			log(LogLevel.Info, message, null);
		}

		@Override
		public void info(String message, Throwable throwable) {
			log(LogLevel.Info, message, throwable);
		}

		@Override
		public void warn(String message) {
			log(LogLevel.Warn, message, null);
		}

		@Override
		public void warn(String message, Throwable throwable) {
			log(LogLevel.Warn, message, throwable);
		}

		@Override
		public void error(String message) {
			log(LogLevel.Error, message, null);
		}

		@Override
		public void error(String message, Throwable throwable) {
			log(LogLevel.Error, message, throwable);
		}

		@Override
		public void fatal(String message) {
			log(LogLevel.Fatal, message, null);
		}

		@Override
		public void fatal(String message, Throwable throwable) {
			log(LogLevel.Fatal, message, throwable);
		}

		private void log(LogLevel logLevel, String message, Throwable throwable) {
			PrintStream stream;
			if (throwable != null) {
				stream = System.err;
			}
			else {
				stream = System.out;
			}

			stream.println(getName() + " - " + logLevel.name() + ": " + message);
			if (throwable != null) {
				throwable.printStackTrace();
			}
		}
	}
}
