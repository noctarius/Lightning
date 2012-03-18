package com.github.lightning.internal.generator;

import java.util.Stack;

import org.objectweb.asm.tree.InsnList;

import com.github.lightning.PropertyDescriptor;
import com.github.lightning.internal.CodeFragmentGenerator;

class ReadPropertyGenerator implements CodeFragmentGenerator {

	private final PropertyDescriptor propertyDescriptor;

	ReadPropertyGenerator(PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
	}

	@Override
	public InsnList generateCodeFragment(Stack<StackValue> localVarStack, Stack<StackValue> operandStack) {
		// TODO Auto-generated method stub
		return null;
	}

}
