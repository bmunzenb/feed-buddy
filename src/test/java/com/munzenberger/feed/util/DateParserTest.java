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

import java.util.Date;

import com.munzenberger.feed.util.DateParser;

import junit.framework.TestCase;

public class DateParserTest extends TestCase {
	
	public void testParseDate() throws Exception {
		
		Date date = DateParser.parse("Sun, 09 Jan 2011 04:00:00 EST");
		assertNotNull(date);
		
		date = DateParser.parse("Sun, 05 Dec 2010 12:30:00 +0000");
		assertNotNull(date);
		
		date = DateParser.parse("1973-01-01T12:00:27.87+00:20");
		assertNotNull(date);
		
		date = DateParser.parse("1985-04-12T23:20:50.52Z");
		assertNotNull(date);
		
		date = DateParser.parse("2011-01-07T23:29:42Z");
		assertNotNull(date);
	}
}
