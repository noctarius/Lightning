package com.github.lightning;

import java.io.DataInput;
import java.io.DataOutput;

import com.github.lightning.base.AbstractMarshaller;
import com.github.lightning.base.AbstractSerializerDefinition;

public class WhatShouldItLookLike {

	public static void main(String[] args) {
		Serializer serializer = Lightning.createSerializer(new BookingEngineSerializerFactory());
		ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

		Serializer remoteSerializer = Lightning.createSerializer(new BookingEngineSerializerFactory());
		remoteSerializer.setClassDefinitionContainer(container);

		Serializer serializer2 = Lightning.newBuilder().serializerDefinitions(new BookingEngineSerializerFactory()).build();
		ClassDefinitionContainer container2 = serializer2.getClassDefinitionContainer();

		Serializer remoteSerializer2 = Lightning.newBuilder().serializerDefinitions(new BookingEngineSerializerFactory()).build();
		remoteSerializer2.setClassDefinitionContainer(container2);
	}

	public static class BookingEngineSerializerFactory extends AbstractSerializerDefinition {

		@Override
		protected void configure() {
			define(Foo.class).byMarshaller(BarMarshaller.class);

			bind(Foo.class).with(Attribute.class).exclude("value");
			bind(Foo.class).field("value").byMarshaller(SomeSpecialIntegerMarshaller.class);
			bind(Foo.class).field("enumValue").byMarshaller(BarMarshaller.class);

			install(SomeChildSerializerFactory.class);
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

	public static class BarMarshaller extends AbstractMarshaller<Bar> {

		@Override
		public void marshall(Bar value, DataOutput dataOutput) {
		}

		@Override
		public Bar unmarshall(Bar value, DataInput dataInput) {
			return value;
		}
	}

	public static class SomeSpecialIntegerMarshaller extends AbstractMarshaller<Integer> {

		@Override
		public void marshall(Integer value, DataOutput dataOutput) {
		}

		@Override
		public Integer unmarshall(Integer value, DataInput dataInput) {
			return value;
		}
	}
}
