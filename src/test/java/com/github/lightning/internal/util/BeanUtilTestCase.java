package com.github.lightning.internal.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BeanUtilTestCase {

	@Test
	public void testPropertyNameCreation() throws Exception {
		String methodName = "isDefined";
		String result = BeanUtil.buildPropertyName(methodName);
		assertEquals("defined", result);

		methodName = "getDefined";
		result = BeanUtil.buildPropertyName(methodName);
		assertEquals("defined", result);

		methodName = "setDefined";
		result = BeanUtil.buildPropertyName(methodName);
		assertEquals("defined", result);

		methodName = "doneDefined";
		result = BeanUtil.buildPropertyName(methodName);
		assertEquals("doneDefined", result);
	}

}
