package com.github.lightning;

@SuppressWarnings("serial")
public class ClassDefinitionNotConstistentException extends RuntimeException {

	public ClassDefinitionNotConstistentException() {
		super();
	}

	public ClassDefinitionNotConstistentException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClassDefinitionNotConstistentException(String message) {
		super(message);
	}

	public ClassDefinitionNotConstistentException(Throwable cause) {
		super(cause);
	}
}
