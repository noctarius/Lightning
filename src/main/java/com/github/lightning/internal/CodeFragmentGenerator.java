package com.github.lightning.internal;

import java.util.Stack;

import org.objectweb.asm.tree.InsnList;

public interface CodeFragmentGenerator {

	InsnList generateCodeFragment(Stack<StackValue> localVarStack, Stack<StackValue> operandStack);

	public static final class StackValue {

		private final String name;
		private final Class<?> type;
		private final byte length;

		public StackValue(String name, Class<?> type, byte length) {
			this.name = name;
			this.type = type;
			this.length = length;
		}

		public String getName() {
			return name;
		}

		public Class<?> getType() {
			return type;
		}

		public byte getLength() {
			return length;
		}
	}

	public static final StackValue THIS = new StackValue("this", Object.class, (byte) 1);
	public static final StackValue FOLLOWUP = new StackValue("", Object.class, (byte) 0);
}
