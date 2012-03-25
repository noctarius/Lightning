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
 package com.github.lightning.maven;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

public class GeneratorTestCase extends AbstractMojoTestCase {

	@Test
	public void testGeneration() throws Exception {
		URL url = getClass().getClassLoader().getResource("generate-pom.xml");
		LightningGeneratorMojo mojo = (LightningGeneratorMojo) lookupMojo("generate", new File(url.toURI()));
		mojo.execute();
	}
}
