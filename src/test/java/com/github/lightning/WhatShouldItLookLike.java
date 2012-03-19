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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;

import com.github.lightning.base.AbstractObjectMarshaller;
import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.logging.LoggerAdapter;

public class WhatShouldItLookLike {

	public static void main(String[] args) {
		Serializer serializer2 = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new BookingEngineSerializerFactory()).build();

		ClassDefinitionContainer container2 = serializer2.getClassDefinitionContainer();

		Serializer remoteSerializer2 = Lightning.newBuilder().logger(new DebugLogger())
				.serializerDefinitions(new BookingEngineSerializerFactory()).build();

		remoteSerializer2.setClassDefinitionContainer(container2);

		Serializer serializer = Lightning.createSerializer(new BookingEngineSerializerFactory());
		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.createSerializer(new BookingEngineSerializerFactory());
		remoteSerializer.setClassDefinitionContainer(container);
	}

	public static class BookingEngineSerializerFactory extends AbstractSerializerDefinition {

		@Override
		protected void configure() {
			define(Bar.class).byMarshaller(new BarMarshaller());

			bind(Foo.class).with(Attribute.class).exclude("value");
			bind(Foo.class).property("value").byMarshaller(SomeSpecialIntegerMarshaller.class);
			bind(Foo.class).property("enumValue").byMarshaller(BarMarshaller.class);

			install(new SomeChildSerializerFactory());
		}
	}

	public static class SomeChildSerializerFactory extends AbstractSerializerDefinition {

		@Override
		public void configure() {
			describesAttributes(Attribute.class);

			bind(Foo.class).attributes(); // like .with(Attribute.class)
		}
	}

	public static class Foo {

		private String first;
		private String second;
		private Integer value;
		private int someOther;
		private Bar enumValue;

		@Attribute(required = true)
		public String getFirst() {
			return first;
		}

		public void setFirst(String first) {
			this.first = first;
		}

		@Attribute
		public String getSecond() {
			return second;
		}

		public void setSecond(String second) {
			this.second = second;
		}

		@Attribute
		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}

		@Attribute
		// Implicitly required
		public int getSomeOther() {
			return someOther;
		}

		public void setSomeOther(int someOther) {
			this.someOther = someOther;
		}

		public Bar getEnumValue() {
			return enumValue;
		}

		public void setEnumValue(Bar enumValue) {
			this.enumValue = enumValue;
		}
	}

	public static enum Bar {
		Value1,
		Value2
	}

	public static @interface Attribute {

		boolean required() default false;
	}

	public static class BarMarshaller extends AbstractObjectMarshaller {

		@Override
		public boolean acceptType(Class<?> type) {
			return type == Bar.class;
		}

		@Override
		public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		}

		@Override
		public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
			return value;
		}
	}

	public static class SomeSpecialIntegerMarshaller extends AbstractObjectMarshaller {

		@Override
		public boolean acceptType(Class<?> type) {
			return type == Integer.class;
		}

		@Override
		public void marshall(Object value, Class<?> type, DataOutput dataOutput) throws IOException {
		}

		@Override
		public <V> V unmarshall(V value, Class<?> type, DataInput dataInput) throws IOException {
			return value;
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
