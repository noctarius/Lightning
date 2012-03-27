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
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import com.github.lightning.base.AbstractObjectMarshaller;
import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.io.SerializerInputStream;
import com.github.lightning.io.SerializerOutputStream;
import com.github.lightning.metadata.ClassDefinitionContainer;
import com.github.lightningtesting.utils.DebugLogger;

public class WhatShouldItLookLike {

	public static void main(String[] args) {
		Serializer serializer2 = Lightning.newBuilder().logger(new DebugLogger()).serializerDefinitions(new BookingEngineSerializerFactory()).build();

		ClassDefinitionContainer container2 = serializer2.getClassDefinitionContainer();

		Serializer remoteSerializer2 = Lightning.newBuilder().logger(new DebugLogger()).serializerDefinitions(new BookingEngineSerializerFactory()).build();

		remoteSerializer2.setClassDefinitionContainer(container2);

		Serializer serializer = Lightning.createSerializer(new BookingEngineSerializerFactory());
		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.createSerializer(new BookingEngineSerializerFactory());
		remoteSerializer.setClassDefinitionContainer(container);

		Foo foo = new Foo();
		foo.enumValue = Bar.Value2;
		foo.first = "first";
		foo.second = "second";
		foo.someOther = 123;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);
		out.writeObject(foo);
		System.out.println(foo);

		byte[] data = baos.toByteArray();
		System.out.println(Arrays.toString(data));

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		SerializerInputStream in = new SerializerInputStream(bais, serializer);
		Object value = in.readObject();
		System.out.println(value);
	}

	public static class BookingEngineSerializerFactory extends AbstractSerializerDefinition {

		@Override
		protected void configure() {
			// define(Bar.class).byMarshaller(new BarMarshaller());

			// bind(Foo.class).with(Attribute.class).exclude("value");
			// bind(Foo.class).property("value").byMarshaller(SomeSpecialIntegerMarshaller.class);
			// bind(Foo.class).property("enumValue").byMarshaller(BarMarshaller.class);

			install(new SomeChildSerializerFactory());
		}
	}

	public static class SomeChildSerializerFactory extends AbstractSerializerDefinition {

		@Override
		public void configure() {
			describesAttributes(Attribute.class);

			bind(Foo.class).attributes();// .exclude("enumValue"); // like
											// .with(Attribute.class)
			// bind(Foo.class).property("enumValue").byMarshaller(new
			// BarMarshaller());
		}
	}

	public static class Foo {

		private String first;
		private String second;
		private Integer value;
		private int someOther;

		@Attribute
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

		@Override
		public String toString() {
			return "Foo [hash=@" + hashCode() + ", first=" + first + ", second=" + second + ", value=" + value + ", someOther=" + someOther + ", enumValue="
					+ enumValue + "]";
		}
	}

	public static enum Bar {
		Value1,
		Value2
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Attribute {

		boolean required() default false;
	}

	public static class BarMarshaller extends AbstractObjectMarshaller {

		@Override
		public boolean acceptType(Class<?> type) {
			return type == Bar.class;
		}

		@Override
		public void marshall(Object value, Class<?> type, DataOutput dataOutput, SerializationContext serializationContext) throws IOException {
		}

		@Override
		public <V> V unmarshall(V value, Class<?> type, DataInput dataInput, SerializationContext serializationContext) throws IOException {
			return null;
		}
	}

	public static class SomeSpecialIntegerMarshaller extends AbstractObjectMarshaller {

		@Override
		public boolean acceptType(Class<?> type) {
			return type == Integer.class;
		}

		@Override
		public void marshall(Object value, Class<?> type, DataOutput dataOutput, SerializationContext serializationContext) throws IOException {
		}

		@Override
		public <V> V unmarshall(V value, Class<?> type, DataInput dataInput, SerializationContext serializationContext) throws IOException {
			return value;
		}

	}
}
