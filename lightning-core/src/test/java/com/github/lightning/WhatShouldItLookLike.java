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
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.github.lightning.base.AbstractObjectMarshaller;
import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.configuration.TypeIntrospector;
import com.github.lightning.generator.PropertyDescriptorFactory;
import com.github.lightning.io.SerializerInputStream;
import com.github.lightning.io.SerializerOutputStream;
import com.github.lightning.metadata.ClassDefinitionContainer;
import com.github.lightning.metadata.PropertyDescriptor;
import com.github.lightningtesting.utils.DebugLogger;

public class WhatShouldItLookLike {

	public static void main(String[] args) {
		Serializer serializer2 = Lightning.newBuilder().logger(new DebugLogger()).serializerDefinitions(new ExampleSerializerDefinition()).build();

		ClassDefinitionContainer container2 = serializer2.getClassDefinitionContainer();

		Serializer remoteSerializer2 = Lightning.newBuilder().logger(new DebugLogger()).serializerDefinitions(new ExampleSerializerDefinition()).build();

		remoteSerializer2.setClassDefinitionContainer(container2);

		Serializer serializer = Lightning.createSerializer(new ExampleSerializerDefinition());
		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.createSerializer(new ExampleSerializerDefinition());
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

	public static class ExampleSerializerDefinition extends AbstractSerializerDefinition {

		@Override
		protected void configure() {
			// Define serializable class using custom implementation of
			// Marshaller
			serialize(Bar.class).using(new BarMarshaller());
			serialize(Bar.class).using(BarMarshaller.class);

			// Define serializable class using annotated members / methods (by
			// usage of Lightning's @com.github.lightning.metadata.Attribute
			// annotation)
			serialize(Foo.class).attributes();
			serialize(Foo.class).attributes().exclude("value");
			serialize(Foo.class).attributes().exclude("value1").exclude("value2");
			serialize(Foo.class).attributes().excludes("value1", "value2");

			// Define serializable class using annotated members / methods (by
			// usage of custom annotation)
			serialize(Foo.class).attributes(Attribute.class);
			serialize(Foo.class).attributes(Attribute.class).exclude("value");
			serialize(Foo.class).attributes(Attribute.class).exclude("value1").exclude("value2");
			serialize(Foo.class).attributes(Attribute.class).excludes("value1", "value2");

			// Define serializable class using custom definition of attributes
			serialize(Foo.class).attributes(attribute("value"), attribute("value").using(SomeSpecialIntegerMarshaller.class),
					attribute("value").using(new SomeSpecialIntegerMarshaller()));

			// Define serializable class using a different implementation of
			// PropertyFinderStrategy
			serialize(Foo.class).using(new FooTypeIntrospector());
			serialize(Foo.class).using(FooTypeIntrospector.class);

			// Install child definition
			install(new SomeChildSerializerDefinition());
		}
	}

	public static class SomeChildSerializerDefinition extends AbstractSerializerDefinition {

		@Override
		public void configure() {
			describesAttributes(Attribute.class);

			serialize(Foo.class).attributes();
		}
	}

	public static class FooTypeIntrospector implements TypeIntrospector {

		@Override
		public List<PropertyDescriptor> introspect(Type type, MarshallerStrategy marshallerStrategy, MarshallerContext marshallerContext,
				PropertyDescriptorFactory propertyDescriptorFactory) {
			// TODO Auto-generated method stub
			return null;
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
