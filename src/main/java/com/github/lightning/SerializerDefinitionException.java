package com.github.lightning;

@SuppressWarnings("serial")
public class SerializerDefinitionException extends RuntimeException {

	public SerializerDefinitionException() {
		super();
	}

	public SerializerDefinitionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializerDefinitionException(String message) {
		super(message);
	}

	public SerializerDefinitionException(Throwable cause) {
		super(cause);
	}
}
