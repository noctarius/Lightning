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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.lightning.Lightning;
import com.github.lightning.Serializer;
import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.metadata.Attribute;
import com.github.lightningtesting.utils.DebugLogger;

public class ListMarshallerTestCase {

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testNoGenericTypeList() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger()).debugCacheDirectory(new File("target"))
				.serializerDefinitions(new AbstractSerializerDefinition() {

					@Override
					protected void configure() {
						bind(NoGenericTypeList.class).attributes();
					}
				}).build();

		List list = new ArrayList();
		list.add("Foo");
		list.add(20);
		list.add(null);
		list.add(BigInteger.TEN);

		NoGenericTypeList value = new NoGenericTypeList();
		value.setList(list);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Object result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);
	}

	@Test
	public void testSimpleGenericTypeList() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger()).debugCacheDirectory(new File("target"))
				.serializerDefinitions(new AbstractSerializerDefinition() {

					@Override
					protected void configure() {
						bind(SimpleGenericTypeList.class).attributes();
					}
				}).build();

		List<String> list = new ArrayList<String>();
		list.add("Foo");
		list.add("Bar");
		list.add(null);
		list.add("Rhabarbar");

		SimpleGenericTypeList value = new SimpleGenericTypeList();
		value.setList(list);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Object result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);
	}

	@Test
	public void testComplexGenericTypeList() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger()).debugCacheDirectory(new File("target"))
				.serializerDefinitions(new AbstractSerializerDefinition() {

					@Override
					protected void configure() {
						bind(ComplexGenericTypeList.class).attributes();
					}
				}).build();

		List<List<String>> list = new ArrayList<List<String>>();

		List<String> list1 = new ArrayList<String>();
		list1.add("Foo");
		list1.add("Bar");
		list1.add(null);
		list1.add("Rhabarbar");

		List<String> list2 = new ArrayList<String>();
		list2.add(null);
		list2.add("Rhabarbar");
		list2.add("Foo");
		list2.add("Bar");

		list.add(list1);
		list.add(list2);

		ComplexGenericTypeList value = new ComplexGenericTypeList();
		value.setList(list);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Object result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);
	}

	@SuppressWarnings("rawtypes")
	public static class NoGenericTypeList {

		@Attribute
		private List list;

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((list == null) ? 0 : list.hashCode());
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
			NoGenericTypeList other = (NoGenericTypeList) obj;
			if (list == null) {
				if (other.list != null)
					return false;
			}
			else if (!list.equals(other.list))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "NoGenericTypeList [list=" + list + "]";
		}
	}

	public static class SimpleGenericTypeList {

		@Attribute
		private List<String> list;

		public List<String> getList() {
			return list;
		}

		public void setList(List<String> list) {
			this.list = list;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((list == null) ? 0 : list.hashCode());
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
			SimpleGenericTypeList other = (SimpleGenericTypeList) obj;
			if (list == null) {
				if (other.list != null)
					return false;
			}
			else if (!list.equals(other.list))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SimpleGenericTypeList [list=" + list + "]";
		}
	}

	public static class ComplexGenericTypeList {

		@Attribute
		private List<List<String>> list;

		public List<List<String>> getList() {
			return list;
		}

		public void setList(List<List<String>> list) {
			this.list = list;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((list == null) ? 0 : list.hashCode());
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
			ComplexGenericTypeList other = (ComplexGenericTypeList) obj;
			if (list == null) {
				if (other.list != null)
					return false;
			}
			else if (!list.equals(other.list))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ComplexGenericTypeList [list=" + list + "]";
		}
	}
}
