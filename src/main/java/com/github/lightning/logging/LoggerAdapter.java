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
package com.github.lightning.logging;

public class LoggerAdapter implements Logger {

	private final String name;

	public LoggerAdapter() {
		this("Default");
	}

	public LoggerAdapter(Class<?> clazz) {
		this(clazz.getCanonicalName());
	}

	public LoggerAdapter(String name) {
		this.name = name;
	}

	@Override
	public Logger getChildLogger(Class<?> clazz) {
		return getChildLogger(clazz.getCanonicalName());
	}

	@Override
	public Logger getChildLogger(final String name) {
		final Logger that = this;
		return new Logger() {

			@Override
			public void warn(String message, Throwable throwable) {
				that.warn(message, throwable);
			}

			@Override
			public void warn(String message) {
				that.warn(message);
			}

			@Override
			public void trace(String message, Throwable throwable) {
				that.trace(message, throwable);
			}

			@Override
			public void trace(String message) {
				that.trace(message);
			}

			@Override
			public boolean isWarnEnabled() {
				return that.isWarnEnabled();
			}

			@Override
			public boolean isTraceEnabled() {
				return that.isTraceEnabled();
			}

			@Override
			public boolean isLogLevelEnabled(LogLevel logLevel) {
				return that.isLogLevelEnabled(logLevel);
			}

			@Override
			public boolean isInfoEnabled() {
				return that.isInfoEnabled();
			}

			@Override
			public boolean isFatalEnabled() {
				return that.isFatalEnabled();
			}

			@Override
			public boolean isErrorEnabled() {
				return that.isErrorEnabled();
			}

			@Override
			public boolean isDebugEnabled() {
				return that.isDebugEnabled();
			}

			@Override
			public void info(String message, Throwable throwable) {
				that.info(message, throwable);
			}

			@Override
			public void info(String message) {
				that.info(message);
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public Logger getChildLogger(String name) {
				return that.getChildLogger(name);
			}

			@Override
			public Logger getChildLogger(Class<?> clazz) {
				return getChildLogger(clazz.getCanonicalName());
			}

			@Override
			public void fatal(String message, Throwable throwable) {
				that.fatal(message, throwable);
			}

			@Override
			public void fatal(String message) {
				that.fatal(message);
			}

			@Override
			public void error(String message, Throwable throwable) {
				that.error(message, throwable);
			}

			@Override
			public void error(String message) {
				that.error(message);
			}

			@Override
			public void debug(String message, Throwable throwable) {
				that.debug(message, throwable);
			}

			@Override
			public void debug(String message) {
				that.debug(message);
			}
		};
	}

	@Override
	public String getName() {
		return name;
	}

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
