package com.github.lightning.logging;


public class LoggerAdapter implements Logger {

	@Override
	public boolean isLogLevelEnabled(LogLevel logLevel) {
		return false;
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public boolean isErrorEnabled() {
		return false;
	}

	@Override
	public boolean isFatalEnabled() {
		return false;
	}

	@Override
	public void trace(String message) {
	}

	@Override
	public void trace(String message, Throwable throwable) {
	}

	@Override
	public void debug(String message) {
	}

	@Override
	public void debug(String message, Throwable throwable) {
	}

	@Override
	public void info(String message) {
	}

	@Override
	public void info(String message, Throwable throwable) {
	}

	@Override
	public void warn(String message) {
	}

	@Override
	public void warn(String message, Throwable throwable) {
	}

	@Override
	public void error(String message) {
	}

	@Override
	public void error(String message, Throwable throwable) {
	}

	@Override
	public void fatal(String message) {
	}

	@Override
	public void fatal(String message, Throwable throwable) {
	}
}
