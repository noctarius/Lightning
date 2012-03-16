package com.github.lightning.internal.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilTestCase {

	@Test
	public void testLowerCamelCase() throws Exception {
		String value = "That is impossible";
		String result = StringUtil.toLowerCamelCase(value);
		assertEquals("thatIsImpossible", result);

		value = "that is impossible";
		result = StringUtil.toLowerCamelCase(value);
		assertEquals("thatIsImpossible", result);

		value = "that   is impossible";
		result = StringUtil.toLowerCamelCase(value);
		assertEquals("thatIsImpossible", result);

		value = "that-is impossible";
		result = StringUtil.toLowerCamelCase(value);
		assertEquals("thatIsImpossible", result);

		value = "that_is impossible";
		result = StringUtil.toLowerCamelCase(value);
		assertEquals("thatIsImpossible", result);
	}

	@Test
	public void testUpperCamelCase() throws Exception {
		String value = "That is impossible";
		String result = StringUtil.toUpperCamelCase(value);
		assertEquals("ThatIsImpossible", result);

		value = "that is impossible";
		result = StringUtil.toUpperCamelCase(value);
		assertEquals("ThatIsImpossible", result);

		value = "that   is impossible";
		result = StringUtil.toUpperCamelCase(value);
		assertEquals("ThatIsImpossible", result);

		value = "that-is impossible";
		result = StringUtil.toUpperCamelCase(value);
		assertEquals("ThatIsImpossible", result);

		value = "that_is impossible";
		result = StringUtil.toUpperCamelCase(value);
		assertEquals("ThatIsImpossible", result);
	}

}
