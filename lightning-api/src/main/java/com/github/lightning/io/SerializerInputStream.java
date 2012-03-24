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
package com.github.lightning.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import com.github.lightning.Serializer;

public class SerializerInputStream extends DataInputStream {

	private final Serializer serializer;

	public SerializerInputStream(InputStream in, Serializer serializer) {
		super(in);
		this.serializer = serializer;
	}

	public <T> T readObject() {
		return serializer.deserialize((DataInput) this);
	}
}
