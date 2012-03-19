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
