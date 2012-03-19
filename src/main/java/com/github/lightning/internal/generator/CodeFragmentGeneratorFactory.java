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
package com.github.lightning.internal.generator;

import com.github.lightning.IllegalAccessorException;
import com.github.lightning.PropertyAccessor.AccessorType;
import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.CodeFragmentGenerator;

public final class CodeFragmentGeneratorFactory {

	private CodeFragmentGeneratorFactory() {
	}

	public static CodeFragmentGenerator getCodeFragmentGenerator(PropertyDescriptor propertyDescriptor, AccessType accessType) {
		if (propertyDescriptor.getPropertyAccessor().getAccessorType() == AccessorType.Field) {
			if (accessType == AccessType.Read) {
				return new ReadPropertyGenerator(propertyDescriptor);
			}

			return new WritePropertyGenerator(propertyDescriptor);
		}

		throw new IllegalAccessorException("No Bytecode Generator found for given strategy");
	}

	public static enum AccessType {
		Read,
		Write
	}
}
