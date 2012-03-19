package com.github.lightning;

import java.lang.annotation.Annotation;

public interface DefinitionVisitor {

	void visitSerializerDefinition(SerializerDefinition serializerDefinition);

	void visitAttributeAnnotation(Class<? extends Annotation> attributeAnnotation);

	void visitClassDefine(Class<?> type, Marshaller marshaller);

	void visitAnnotatedAttribute(PropertyDescriptor propertyDescriptor, Marshaller marshaller);

	void visitPropertyDescriptor(PropertyDescriptor propertyDescriptor, Marshaller marshaller);

	void visitFinalizeSerializerDefinition(SerializerDefinition serializerDefinition);

}
