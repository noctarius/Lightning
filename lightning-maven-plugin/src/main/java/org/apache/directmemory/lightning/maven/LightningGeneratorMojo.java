/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directmemory.lightning.maven;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.tools.SimpleJavaFileObject;

import org.apache.directmemory.lightning.SerializationStrategy;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.base.AbstractSerializerDefinition;
import org.apache.directmemory.lightning.configuration.SerializerDefinition;
import org.apache.directmemory.lightning.logging.LogLevel;
import org.apache.directmemory.lightning.logging.Logger;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;


/**
 * Generates sourcecode of native marshallers for Lightning {@link Serializer}
 * by exploring all source {@link SerializerDefinition} files.
 * 
 * @goal generate
 * @lifecycle process-classes
 * @phase process-classes
 * @execute phase="process-classes"
 * @execute goal="process-classes:generate"
 * @requiresDependencyResolution compile
 * @requiresProject true
 * @threadSafe true
 */
public class LightningGeneratorMojo extends AbstractCompilerMojo {

	/**
	 * The current build session instance. This is used for
	 * toolchain manager API calls.
	 * 
	 * @parameter default-value="${session}"
	 * @required
	 * @readonly
	 */
	private MavenSession session;

	/**
	 * The java generated-source directory.
	 * 
	 * @parameter
	 *            default-value=
	 *            "${project.build.directory}/generated-sources/lightning"
	 */
	private File generatedSourceDirectory;

	/**
	 * The directory where compiled classes resist.
	 * 
	 * @parameter default-value="${project.build.directory}/classes"
	 */
	private File targetBuildDirectory;

	/**
	 * Project classpath.
	 * 
	 * @parameter default-value="${project.compileClasspathElements}"
	 * @required
	 * @readonly
	 */
	private List<String> classpathElements;

	/**
	 * The generator strategy.
	 * 
	 * @parameter default-value="speed"
	 */
	private String strategy;

	@Override
	public void execute() throws MojoExecutionException, CompilationFailureException {
		if (encoding == null) {
			encoding = "UTF-8";
		}

		SerializationStrategy serializationStrategy = "size".equalsIgnoreCase(strategy) ? SerializationStrategy.SizeOptimized
				: SerializationStrategy.SpeedOptimized;

		MavenLoggerAdapter logger = new MavenLoggerAdapter(LightningGeneratorMojo.class.getCanonicalName());
		getLog().info("Searching in path " + targetBuildDirectory.getAbsolutePath());
		List<File> files = SupportUtil.recursiveGetAllJavaSources(targetBuildDirectory, new ArrayList<File>(), fileFilter);

		List<URL> urlClasspathElements = new ArrayList<URL>();
		if (getClasspathElements() != null) {
			for (String classpathElement : getClasspathElements()) {
				try {
					URL url = new File(classpathElement).toURI().toURL();
					urlClasspathElements.add(url);
				}
				catch (Exception e) {
					// Intentionally left blank
				}
			}
		}
		ClassLoader classLoader = new URLClassLoader(urlClasspathElements.toArray(new URL[urlClasspathElements.size()]), getClass().getClassLoader());

		for (File file : files) {
			try {
				String className = file.getAbsolutePath().replace(targetBuildDirectory.getAbsolutePath(), "");
				if (className.startsWith("/") || className.startsWith("\\")) {
					className = className.substring(1);
				}

				className = className.replace(".class", "").replace("/", ".").replace("\\", ".");

				getLog().debug("Trying class " + className);
				Class<?> clazz = classLoader.loadClass(className);
				if (AbstractSerializerDefinition.class.isAssignableFrom(clazz)) {
					getLog().debug("Found SerializerDefinition in class " + className);

					AbstractSerializerDefinition definition = (AbstractSerializerDefinition) clazz.newInstance();

					SerializerDefinitionAnalyser analyser = new SerializerDefinitionAnalyser(logger);
					analyser.analyse(definition);
					analyser.build(generatedSourceDirectory, serializationStrategy, encoding);
				}
			}
			catch (Exception e) {
				logger.error("Could not generate Lightning source for file " + file.getName(), e);
			}
		}

		super.execute();

		// session.getCurrentProject().addCompileSourceRoot(generatedSourceDirectory.getAbsolutePath());
	}

	@Override
	protected SourceInclusionScanner getSourceInclusionScanner(int staleMillis) {
		return new StaleSourceScanner(staleMillis);
	}

	@Override
	protected SourceInclusionScanner getSourceInclusionScanner(String inputFileEnding) {
		return new SimpleSourceInclusionScanner(Collections.singleton("**/*.java"), Collections.EMPTY_SET);
	}

	@Override
	protected List<String> getClasspathElements() {
		return classpathElements;
	}

	@Override
	protected List<String> getCompileSourceRoots() {
		return Collections.singletonList(generatedSourceDirectory.getAbsolutePath());
	}

	@Override
	protected File getOutputDirectory() {
		return targetBuildDirectory;
	}

	@Override
	protected String getSource() {
		return source;
	}

	@Override
	protected String getTarget() {
		return target;
	}

	@Override
	protected String getCompilerArgument() {
		return compilerArgument;
	}

	@Override
	protected Map<String, String> getCompilerArguments() {
		return compilerArguments;
	}

	@Override
	protected File getGeneratedSourcesDirectory() {
		return generatedSourceDirectory;
	}

	private final FileFilter fileFilter = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().endsWith(".class");
		}
	};

	private class FileObject extends SimpleJavaFileObject {

		private final Charset charset;
		private final File file;

		private FileObject(File file, Charset charset) {
			super(file.toURI(), Kind.SOURCE);
			this.charset = charset;
			this.file = file;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return SupportUtil.readAllText(file, charset);
		}
	}

	private class MavenLoggerAdapter implements Logger {

		private final String name;

		private MavenLoggerAdapter(String name) {
			this.name = name;
		}

		@Override
		public Logger getChildLogger(Class<?> clazz) {
			return getChildLogger(clazz.getCanonicalName());
		}

		@Override
		public Logger getChildLogger(String name) {
			return new MavenLoggerAdapter(name);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isLogLevelEnabled(LogLevel logLevel) {
			if (logLevel == LogLevel.Debug) {
				return getLog().isDebugEnabled();
			}

			if (logLevel == LogLevel.Error) {
				return getLog().isErrorEnabled();
			}

			if (logLevel == LogLevel.Fatal) {
				return getLog().isErrorEnabled();
			}

			if (logLevel == LogLevel.Trace) {
				return getLog().isDebugEnabled();
			}

			if (logLevel == LogLevel.Warn) {
				return getLog().isWarnEnabled();
			}

			return getLog().isInfoEnabled();
		}

		@Override
		public boolean isTraceEnabled() {
			return isLogLevelEnabled(LogLevel.Trace);
		}

		@Override
		public boolean isDebugEnabled() {
			return isLogLevelEnabled(LogLevel.Debug);
		}

		@Override
		public boolean isInfoEnabled() {
			return isLogLevelEnabled(LogLevel.Info);
		}

		@Override
		public boolean isWarnEnabled() {
			return isLogLevelEnabled(LogLevel.Warn);
		}

		@Override
		public boolean isErrorEnabled() {
			return isLogLevelEnabled(LogLevel.Error);
		}

		@Override
		public boolean isFatalEnabled() {
			return isLogLevelEnabled(LogLevel.Fatal);
		}

		@Override
		public void trace(String message) {
			trace(message, null);
		}

		@Override
		public void trace(String message, Throwable throwable) {
			debug(message, throwable);
		}

		@Override
		public void debug(String message) {
			debug(message, null);
		}

		@Override
		public void debug(String message, Throwable throwable) {
			getLog().debug(message, throwable);
		}

		@Override
		public void info(String message) {
			info(message, null);
		}

		@Override
		public void info(String message, Throwable throwable) {
			getLog().info(message, throwable);
		}

		@Override
		public void warn(String message) {
			warn(message, null);
		}

		@Override
		public void warn(String message, Throwable throwable) {
			getLog().warn(message, throwable);
		}

		@Override
		public void error(String message) {
			error(message, null);
		}

		@Override
		public void error(String message, Throwable throwable) {
			getLog().error(message, throwable);
		}

		@Override
		public void fatal(String message) {
			fatal(message, null);
		}

		@Override
		public void fatal(String message, Throwable throwable) {
			error(message, throwable);
		}
	}
}
