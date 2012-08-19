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
package com.github.lightning.internal.marshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Test;

import com.github.lightning.Lightning;
import com.github.lightning.Serializer;
import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.metadata.Attribute;
import com.github.lightningtesting.utils.DebugLogger;

public class ShortMarshallerTestCase {

	@Test
	public void testShortPrimitive() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger()).debugCacheDirectory(new File("target"))
				.serializerDefinitions(new AbstractSerializerDefinition() {

					@Override
					protected void configure() {
						serialize(PrimitiveHolder.class).attributes();
					}
				}).build();

		PrimitiveHolder value = new PrimitiveHolder();
		value.setValue1((short) 0);
		value.setValue2(Short.MAX_VALUE);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Object result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);

		value = new PrimitiveHolder();
		value.setValue1((short) -10);
		value.setValue2((short) 20);

		baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		bais = new ByteArrayInputStream(baos.toByteArray());
		result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);
	}

	@Test
	public void testShortWrapper() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger()).debugCacheDirectory(new File("target"))
				.serializerDefinitions(new AbstractSerializerDefinition() {

					@Override
					protected void configure() {
						serialize(WrapperHolder.class).attributes();
					}
				}).build();

		WrapperHolder value = new WrapperHolder();
		value.setValue1(Short.MAX_VALUE);
		value.setValue2(null);
		value.setValue3((short) 34);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Object result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);

		value = new WrapperHolder();
		value.setValue1((short) 0);
		value.setValue2(Short.MIN_VALUE);
		value.setValue3(null);

		baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		bais = new ByteArrayInputStream(baos.toByteArray());
		result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);

		value = new WrapperHolder();
		value.setValue1(null);
		value.setValue2((short) -1);
		value.setValue3(Short.MAX_VALUE);

		baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		bais = new ByteArrayInputStream(baos.toByteArray());
		result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);
	}

	public static class PrimitiveHolder {

		@Attribute
		private short value1;

		@Attribute
		private short value2;

		public short isValue1() {
			return value1;
		}

		public void setValue1(short value1) {
			this.value1 = value1;
		}

		public short isValue2() {
			return value2;
		}

		public void setValue2(short value2) {
			this.value2 = value2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + value1;
			result = prime * result + value2;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PrimitiveHolder other = (PrimitiveHolder) obj;
			if (value1 != other.value1)
				return false;
			if (value2 != other.value2)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "PrimitiveHolder [value1=" + value1 + ", value2=" + value2 + "]";
		}
	}

	public static class WrapperHolder {

		@Attribute
		private Short value1;

		@Attribute
		private Short value2;

		@Attribute
		private Short value3;

		public Short getValue1() {
			return value1;
		}

		public void setValue1(Short value1) {
			this.value1 = value1;
		}

		public Short getValue2() {
			return value2;
		}

		public void setValue2(Short value2) {
			this.value2 = value2;
		}

		public Short getValue3() {
			return value3;
		}

		public void setValue3(Short value3) {
			this.value3 = value3;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
			result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
			result = prime * result + ((value3 == null) ? 0 : value3.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WrapperHolder other = (WrapperHolder) obj;
			if (value1 == null) {
				if (other.value1 != null)
					return false;
			}
			else if (!value1.equals(other.value1))
				return false;
			if (value2 == null) {
				if (other.value2 != null)
					return false;
			}
			else if (!value2.equals(other.value2))
				return false;
			if (value3 == null) {
				if (other.value3 != null)
					return false;
			}
			else if (!value3.equals(other.value3))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "WrapperHolder [value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + "]";
		}
	}
}
