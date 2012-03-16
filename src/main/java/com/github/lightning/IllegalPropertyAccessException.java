package com.github.lightning;

public class IllegalPropertyAccessException extends RuntimeException {

	public IllegalPropertyAccessException() {
		super();
	}

	public IllegalPropertyAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalPropertyAccessException(String message) {
		super(message);
	}

	public IllegalPropertyAccessException(Throwable cause) {
		super(cause);
	}
}
