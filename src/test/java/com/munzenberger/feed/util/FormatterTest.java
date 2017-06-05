/*
 * Copyright 2017 Brian Munzenberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.munzenberger.feed.util;

import junit.framework.TestCase;

public class FormatterTest extends TestCase {

	private static final long ONE_SECOND = 1000;
	private static final long ONE_MINUTE = 60 * ONE_SECOND;
	private static final long ONE_HOUR = 60 * ONE_MINUTE;
	
	public void testElapsedTime() {
		
		assertEquals("1 ms", Formatter.elapsedTime(1));
		assertEquals("1 sec", Formatter.elapsedTime(ONE_SECOND));
		assertEquals("1 min 0 sec", Formatter.elapsedTime(ONE_MINUTE));
		assertEquals("1 hr 0 min 0 sec", Formatter.elapsedTime(ONE_HOUR));
		
		assertEquals("1 sec", Formatter.elapsedTime(ONE_SECOND + 1));
		assertEquals("1 min 1 sec", Formatter.elapsedTime(ONE_MINUTE + ONE_SECOND));
		assertEquals("1 min 1 sec", Formatter.elapsedTime(ONE_MINUTE + ONE_SECOND + 1));
		assertEquals("1 hr 1 min 1 sec", Formatter.elapsedTime(ONE_HOUR + ONE_MINUTE + ONE_SECOND));
		assertEquals("1 hr 1 min 1 sec", Formatter.elapsedTime(ONE_HOUR + ONE_MINUTE + ONE_SECOND + 1));
	}
}
