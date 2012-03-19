package com.github.lightning.logging;

public interface Logger {

	public static enum LogLevel {
		Trace,
		Debug,
		Info,
		Warn,
		Error,
		Fatal
	}

	Logger getChildLogger(Class<?> clazz);

	Logger getChildLogger(String name);

	String getName();

	boolean isLogLevelEnabled(LogLevel logLevel);

	boolean isTraceEnabled();

	boolean isDebugEnabled();

	boolean isInfoEnabled();

	boolean isWarnEnabled();

	boolean isErrorEnabled();

	boolean isFatalEnabled();

	void trace(String message);

	void trace(String message, Throwable throwable);

	void debug(String message);

	void debug(String message, Throwable throwable);

	void info(String message);

	void info(String message, Throwable throwable);

	void warn(String message);

	void warn(String message, Throwable throwable);

	void error(String message);

	void error(String message, Throwable throwable);

	void fatal(String message);

	void fatal(String message, Throwable throwable);

}
