/**
 * Copyright 2006-2009 the original author or authors.
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
package com.github.lightning.internal.instantiator;

import com.github.lightning.internal.instantiator.strategy.StdInstantiatorStrategy;

/**
 * Objenesis implementation using the
 * {@link com.github.lightning.internal.instantiator.strategy.StdInstantiatorStrategy}
 * .
 * 
 * @author Henri Tremblay
 */
public class ObjenesisStd extends ObjenesisBase {

	/**
	 * Default constructor using the
	 * {@link com.github.lightning.internal.instantiator.strategy.StdInstantiatorStrategy}
	 */
	public ObjenesisStd() {
		super(new StdInstantiatorStrategy());
	}

	/**
	 * Instance using the
	 * {@link com.github.lightning.internal.instantiator.strategy.StdInstantiatorStrategy}
	 * with or without
	 * caching
	 * {@link com.github.lightning.ObjectInstantiator}s
	 * 
	 * @param useCache
	 *            If
	 *            {@link com.github.lightning.ObjectInstantiator}
	 *            s should be cached
	 */
	public ObjenesisStd(boolean useCache) {
		super(new StdInstantiatorStrategy(), useCache);
	}
}
