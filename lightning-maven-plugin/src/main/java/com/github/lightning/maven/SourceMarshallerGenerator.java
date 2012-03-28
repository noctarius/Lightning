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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.github.lightning.SerializationStrategy;
import com.github.lightning.logging.Logger;
import com.github.lightning.metadata.PropertyDescriptor;

public class SourceMarshallerGenerator {

	private final VelocityEngine engine;
	private final Template marshallerTemplate;
	private final Charset charset;
	private final Logger logger;

	public SourceMarshallerGenerator(Charset charset, Logger logger) throws IOException {
		this.charset = charset;
		this.logger = logger;

		Properties properties = new Properties();
		InputStream stream = getClass().getClassLoader().getResourceAsStream("velocity.properties");
		properties.load(stream);
		engine = new VelocityEngine(properties);

		engine.init();
		marshallerTemplate = engine.getTemplate("marshaller.vm", "UTF-8");
	}

	public File generateMarshaller(Class<?> type, List<PropertyDescriptor> propertyDescriptors,
			SerializationStrategy serializationStrategy, File outputFolder) throws IOException {

		// Copy properties and sort them by name
		List<PropertyDescriptor> propertyDescriptorsCopy = new ArrayList<PropertyDescriptor>(propertyDescriptors);
		Collections.sort(propertyDescriptorsCopy);

		String packageName = type.getPackage() != null ? type.getPackage().getName() : "lightning";
		String className = type.getName().replace(packageName + ".", "") + "LightningGeneratedMarshaller";

		File packageFolder = new File(outputFolder, packageName.replace(".", "/"));
		if (!packageFolder.exists()) {
			packageFolder.mkdirs();
		}

		File outputFile = new File(packageFolder, className + ".java");

		logger.info("Generating source :" + outputFile.getAbsolutePath());

		FileOutputStream stream = new FileOutputStream(outputFile);
		OutputStreamWriter writer = new OutputStreamWriter(stream, charset);

		VelocityContext context = new VelocityContext();

		context.put("support", new Support());
		context.put("packageName", packageName);
		context.put("className", className);
		context.put("properties", propertyDescriptorsCopy);
		context.put("strategy", serializationStrategy.name());

		marshallerTemplate.merge(context, writer);

		writer.flush();
		writer.close();

		return outputFile;
	}

	public static class Support {

		public String toFinalFieldName(String prefix, PropertyDescriptor propertyDescriptor) {
			return new StringBuilder(prefix.toUpperCase()).append("_").append(propertyDescriptor.getPropertyName()
					.toUpperCase()).append("_LIGHTNING").toString();
		}

		public String generateWriter(PropertyDescriptor propertyDescriptor, String instanceName) {
			StringBuilder sb = new StringBuilder(propertyDescriptor.getPropertyName()).append("PropertyAccessor.write");
			Class<?> type = propertyDescriptor.getType();
			if (type == boolean.class) {
				sb.append("Boolean(").append(instanceName).append(", ((Boolean) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").booleanValue())");
			}
			else if (type == byte.class) {
				sb.append("Byte(").append(instanceName).append(", ((Byte) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").byteValue())");
			}
			else if (type == char.class) {
				sb.append("Char(").append(instanceName).append(", ((Character) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").charValue())");
			}
			else if (type == short.class) {
				sb.append("Short(").append(instanceName).append(", ((Short) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").shortValue())");
			}
			else if (type == int.class) {
				sb.append("Int(").append(instanceName).append(", ((Integer) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").intValue())");
			}
			else if (type == long.class) {
				sb.append("Long(").append(instanceName).append(", ((Long) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").longValue())");
			}
			else if (type == float.class) {
				sb.append("Float(").append(instanceName).append(", ((Float) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").floatValue())");
			}
			else if (type == double.class) {
				sb.append("Double(").append(instanceName).append(", ((Double) ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(").doubleValue())");
			}
			else {
				sb.append("Object(").append(instanceName).append(", ").append(propertyDescriptor.getPropertyName())
						.append("Value").append(")");
			}

			return sb.append(";").toString();
		}

		public String generateReader(PropertyDescriptor propertyDescriptor) {
			StringBuilder sb = new StringBuilder();
			Class<?> type = propertyDescriptor.getType();
			if (type == boolean.class) {
				sb.append("Boolean.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readBoolean(");
			}
			else if (type == byte.class) {
				sb.append("Byte.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readByte(");
			}
			else if (type == char.class) {
				sb.append("Character.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readChar(");
			}
			else if (type == short.class) {
				sb.append("Short.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readShort(");
			}
			else if (type == int.class) {
				sb.append("Integer.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readInt(");
			}
			else if (type == long.class) {
				sb.append("Long.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readLong(");
			}
			else if (type == float.class) {
				sb.append("Float.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readFloat(");
			}
			else if (type == double.class) {
				sb.append("Double.valueOf(").append(propertyDescriptor.getPropertyName()).append("PropertyAccessor")
						.append(".readDouble(");
			}
			else {
				sb.append(propertyDescriptor.getPropertyName()).append("PropertyAccessor").append(".readObject(");
			}

			sb.append("value)");

			if (type.isPrimitive()) {
				sb.append(")");
			}

			return sb.toString();
		}
	}
}
