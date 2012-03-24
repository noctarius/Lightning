package com.github.lightning.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import com.github.lightning.ClassDefinitionContainerTestCase.SerializerDefinition;
import com.github.lightning.Serializer;
import com.github.lightning.maven.parser.JavaParser;

/**
 * Generates sourcecode of native marshallers for Lightning {@link Serializer}
 * by exploring all source {@link SerializerDefinition} files.
 * 
 * @goal generate
 * @lifecycle compile
 * @phase generate-sources
 * @execute phase="generate-sources"
 * @execute goal="compile:generate"
 * @requiresProject true
 * @threadSafe true
 */
public class LightningGeneratorMojo extends AbstractMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The maven session.
	 * 
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	private MavenSession session;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 * @readonly
	 */
	private MavenProjectHelper projectHelper;

	/**
	 * The java sources directory.
	 * 
	 * @parameter default-value="${project.build.sourceDirectory}"
	 * @readonly
	 */
	private File sourceDirectory;

	/**
	 * The java generated-source directory.
	 * 
	 * @parameter
	 *            default-value=
	 *            "${project.build.directory}/generated-sources/lightning"
	 */
	private File generatedSourceDirectory;

	/**
	 * The file encoding to use for source files.
	 * 
	 * @parameter default-value="${project.build.sourceEncoding}"
	 */
	private String encoding;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		JavaParser parser = Parboiled.createParser(JavaParser.class);
		List<File> sourceFiles = recursiveGetAllJavaSources(sourceDirectory, new ArrayList<File>());

		Rule rule = parser.CompilationUnit();
		for (File sourceFile : sourceFiles) {
			String sourcecode = readAllText(sourceFile, Charset.forName(encoding));
			
			ParsingResult<?> result = new RecoveringParseRunner(rule).run(sourcecode);
			
			Node<?> root = result.parseTreeRoot;
		}
	}

	private static List<File> recursiveGetAllJavaSources(File file, ArrayList<File> list) {
		if (file.isDirectory()) {
			for (File f : file.listFiles(fileFilter)) {
				recursiveGetAllJavaSources(f, list);
			}
		}
		else {
			list.add(file);
		}
		return list;
	}

	public static String readAllText(File file, Charset charset) {
		try {
			StringBuilder sb = new StringBuilder();
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	private static final FileFilter fileFilter = new FileFilter() {

		public boolean accept(File file) {
			return file.isDirectory() || file.getName().endsWith(".java");
		}
	};
}
