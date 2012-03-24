package com.github.lightning.exceptions;

@SuppressWarnings("serial")
public class SerializerExecutionException extends RuntimeException {

	public SerializerExecutionException() {
		super();
	}

	public SerializerExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializerExecutionException(String message) {
		super(message);
	}

	public SerializerExecutionException(Throwable cause) {
		super(cause);
	}
}
