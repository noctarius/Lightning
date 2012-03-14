package com.github.lightning;

import com.github.lightning.base.AbstractMarshaller;
import com.github.lightning.base.AbstractSerializerFactory;

public class WhatShouldItLookLike {

	public class BookingEngineSerializerFactory extends AbstractSerializerFactory {

		@Override
		public void configure() {
			bind(Foo.class).with(Attribute.class).exclude("value");
			bind(Foo.class).field("value").byMarshaller(SomeSpecialIntegerMarshaller.class);
			bind(Foo.class).field("enumValue").byMarshaller(BarMarshaller.class);

			install(SomeChildSerializerFactory.class);
		}
	}

	public class SomeChildSerializerFactory extends AbstractSerializerFactory {

		@Override
		public void configure() {
			bind(Foo.class).attributes(); // like .with(Attribute.class)
		}
	}

	public class Foo {

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

	public enum Bar {
		Value1,
		Value2
	}

	public @interface Attribute {

		boolean required() default false;
	}

	public class BarMarshaller extends AbstractMarshaller {

	}

	public class SomeSpecialIntegerMarshaller extends AbstractMarshaller {

	}
}
