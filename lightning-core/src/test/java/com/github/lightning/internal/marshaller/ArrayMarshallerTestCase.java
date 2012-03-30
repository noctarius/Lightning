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
import java.util.Random;

import org.junit.Test;

import com.github.lightning.Lightning;
import com.github.lightning.Serializer;
import com.github.lightning.base.AbstractSerializerDefinition;
import com.github.lightning.io.SerializerInputStream;
import com.github.lightning.io.SerializerOutputStream;
import com.github.lightning.metadata.Attribute;

public class ArrayMarshallerTestCase {

	private static final Random RANDOM = new Random(-System.nanoTime());

	@Test
	public void testBooleanArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(BooleanArray.class).attributes();
			}
		});

		BooleanArray test = new BooleanArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((boolean[]) array)[index] = RANDOM.nextBoolean();
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		BooleanArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testByteArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(ByteArray.class).attributes();
			}
		});

		ByteArray test = new ByteArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((byte[]) array)[index] = (byte) (RANDOM.nextInt(256) - 127);
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		ByteArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testCharArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(CharArray.class).attributes();
			}
		});

		CharArray test = new CharArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((char[]) array)[index] = (char) (RANDOM.nextInt(256) - 127);
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		CharArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testShortArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(ShortArray.class).attributes();
			}
		});

		ShortArray test = new ShortArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((short[]) array)[index] = (short) (RANDOM.nextInt(256) - 127);
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		ShortArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testIntArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(IntArray.class).attributes();
			}
		});

		IntArray test = new IntArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((int[]) array)[index] = RANDOM.nextInt();
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		IntArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testLongArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(LongArray.class).attributes();
			}
		});

		LongArray test = new LongArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((long[]) array)[index] = RANDOM.nextLong();
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		LongArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testFloatArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(FloatArray.class).attributes();
			}
		});

		FloatArray test = new FloatArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((float[]) array)[index] = RANDOM.nextFloat();
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		FloatArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testDoubleArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(DoubleArray.class).attributes();
			}
		});

		DoubleArray test = new DoubleArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((double[]) array)[index] = RANDOM.nextDouble();
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		DoubleArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	@Test
	public void testObjectArrayMarshalling() throws Exception {
		Serializer serializer = Lightning.createSerializer(new AbstractSerializerDefinition() {

			@Override
			protected void configure() {
				bind(ObjectArray.class).attributes();
			}
		});

		ObjectArray test = new ObjectArray();
		fillArray(new Predicate() {

			@Override
			public void execute(Object array, int index) {
				((Object[]) array)[index] = "Hello-" + RANDOM.nextInt();
			}
		}, test.array);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializerOutputStream out = new SerializerOutputStream(baos, serializer);

		out.writeObject(test);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		SerializerInputStream in = new SerializerInputStream(bais, serializer);

		ObjectArray result = in.readObject();

		assertNotNull(result);
		assertEquals(test, result);
	}

	private static void fillArray(Predicate predicate, Object array) {
		for (int i = 0; i < 10; i++) {
			predicate.execute(array, i);
		}
	}

	private static interface Predicate {

		void execute(Object array, int index);
	}

	public static class BooleanArray {

		@Attribute
		private boolean[] array = new boolean[10];

		public boolean[] getArray() {
			return array;
		}

		public void setArray(boolean[] array) {
			this.array = array;
		}
	}

	public static class ByteArray {

		@Attribute
		private byte[] array = new byte[10];

		public byte[] getArray() {
			return array;
		}

		public void setArray(byte[] array) {
			this.array = array;
		}
	}

	public static class CharArray {

		@Attribute
		private char[] array = new char[10];

		public char[] getArray() {
			return array;
		}

		public void setArray(char[] array) {
			this.array = array;
		}
	}

	public static class ShortArray {

		@Attribute
		private short[] array = new short[10];

		public short[] getArray() {
			return array;
		}

		public void setArray(short[] array) {
			this.array = array;
		}
	}

	public static class IntArray {

		@Attribute
		private int[] array = new int[10];

		public int[] getArray() {
			return array;
		}

		public void setArray(int[] array) {
			this.array = array;
		}
	}

	public static class LongArray {

		@Attribute
		private long[] array = new long[10];

		public long[] getArray() {
			return array;
		}

		public void setArray(long[] array) {
			this.array = array;
		}
	}

	public static class FloatArray {

		@Attribute
		private float[] array = new float[10];

		public float[] getArray() {
			return array;
		}

		public void setArray(float[] array) {
			this.array = array;
		}
	}

	public static class DoubleArray {

		@Attribute
		private double[] array = new double[10];

		public double[] getArray() {
			return array;
		}

		public void setArray(double[] array) {
			this.array = array;
		}
	}

	public static class ObjectArray {

		@Attribute
		private Object[] array = new Object[10];

		public Object[] getArray() {
			return array;
		}

		public void setArray(Object[] array) {
			this.array = array;
		}
	}
}
