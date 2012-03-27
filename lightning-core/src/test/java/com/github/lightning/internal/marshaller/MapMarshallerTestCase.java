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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.github.lightning.Lightning;
import com.github.lightning.Serializer;
import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.metadata.Attribute;
import com.github.lightningtesting.utils.DebugLogger;

public class MapMarshallerTestCase {

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testNoGenericTypeList() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger()).debugCacheDirectory(new File("target"))
				.serializerDefinitions(new AbstractSerializerDefinition() {

					@Override
					protected void configure() {
						bind(NoGenericTypeMap.class).attributes();
					}
				}).build();

		Map map = new HashMap();
		map.put("Foo", 20);
		map.put(21, "foo");
		map.put("bar", null);
		map.put(44, BigInteger.TEN);

		NoGenericTypeMap value = new NoGenericTypeMap();
		value.setMap(map);

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
						bind(SimpleGenericTypeMap.class).attributes();
					}
				}).build();

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("Foo", 21);
		map.put("Bar", Integer.MAX_VALUE);
		map.put("Rhabarbar", null);

		SimpleGenericTypeMap value = new SimpleGenericTypeMap();
		value.setMap(map);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Object result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);
	}

	@Test
	@Ignore
	public void testComplexGenericTypeList() throws Exception {
		Serializer serializer = Lightning.newBuilder().logger(new DebugLogger()).debugCacheDirectory(new File("target"))
				.serializerDefinitions(new AbstractSerializerDefinition() {

					@Override
					protected void configure() {
						bind(ComplexGenericTypeSet.class).attributes();
					}
				}).build();

		Set<Set<String>> set = new HashSet<Set<String>>();

		Set<String> set1 = new HashSet<String>();
		set1.add("Foo");
		set1.add("Bar");
		set1.add(null);
		set1.add("Rhabarbar");

		Set<String> set2 = new HashSet<String>();
		set2.add(null);
		set2.add("Rhabarbar");
		set2.add("Foo");
		set2.add("Bar");

		set.add(set1);
		set.add(set2);

		ComplexGenericTypeSet value = new ComplexGenericTypeSet();
		value.setSet(set);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(value, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Object result = serializer.deserialize(bais);

		assertNotNull(result);
		assertEquals(value, result);
	}

	@SuppressWarnings("rawtypes")
	public static class NoGenericTypeMap {

		@Attribute
		private Map map;

		public Map getmap() {
			return map;
		}

		public void setMap(Map map) {
			this.map = map;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((map == null) ? 0 : map.hashCode());
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
			NoGenericTypeMap other = (NoGenericTypeMap) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			}
			else if (!map.equals(other.map))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "NoGenericTypeMap [map=" + map + "]";
		}
	}

	public static class SimpleGenericTypeMap {

		@Attribute
		private Map<String, Integer> map;

		public Map<String, Integer> getMap() {
			return map;
		}

		public void setMap(Map<String, Integer> map) {
			this.map = map;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((map == null) ? 0 : map.hashCode());
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
			SimpleGenericTypeMap other = (SimpleGenericTypeMap) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			}
			else if (!map.equals(other.map))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SimpleGenericTypeMap [map=" + map + "]";
		}
	}

	public static class ComplexGenericTypeSet {

		@Attribute
		private Set<Set<String>> set;

		public Set<Set<String>> getSet() {
			return set;
		}

		public void setSet(Set<Set<String>> set) {
			this.set = set;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((set == null) ? 0 : set.hashCode());
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
			ComplexGenericTypeSet other = (ComplexGenericTypeSet) obj;
			if (set == null) {
				if (other.set != null)
					return false;
			}
			else if (!set.equals(other.set))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ComplexGenericTypeSet [set=" + set + "]";
		}
	}
}
