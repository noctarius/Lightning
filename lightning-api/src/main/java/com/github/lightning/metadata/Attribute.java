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
package com.github.lightning.metadata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a property (annotated field or method) as an attribute to be
 * serialized by Lightning.<br>
 * 
 * <pre>
 * 
 * public class MyEntity {
 * 
 * 	private long id;
 * 
 * 	&#064;Attribute
 * 	private String name;
 * 
 * 	&#064;Attribute
 * 	public long getId() {
 * 		return id;
 * 	}
 * 
 * 	public void setId(long id) {
 * 		this.id = id;
 * 	}
 * 
 * 	public String getName() {
 * 		return name;
 * 	}
 * 
 * 	public void setName(String name) {
 * 		this.name = name;
 * 	}
 * }
 * </pre>
 * 
 * @author noctarius
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Attribute {

	public static final String NULL = "~~NULL~~";

	/**
	 * If a method is annotated this value defines a property name differs from
	 * property name extracted from methodname.<br>
	 * Means if method is "getFoo" property name defaults to "foo", but if the
	 * method is annotated by @Attribute("bar") the property name will be
	 * explicitly "bar".
	 * 
	 * @return the defined property name
	 */
	String property() default NULL;

	boolean nullable() default false;

}
