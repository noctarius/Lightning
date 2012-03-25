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
package com.github.lightning;

/**
 * Defines the comparison strategy of classes between different serializers. The
 * standard strategy of Java is SerialVersionUID but Lightning has some lighter
 * algorithm which only takes properties into account.
 * 
 * @author noctarius
 */
public enum ClassComparisonStrategy {

	/**
	 * Default Java Serialization like SerialVersionUID
	 */
	SerialVersionUID,

	/**
	 * Lightning checksum calculation
	 */
	LightningChecksum

}
