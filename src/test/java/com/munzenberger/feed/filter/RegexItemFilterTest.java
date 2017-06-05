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
package com.munzenberger.feed.filter;

import com.munzenberger.feed.parser.rss.Item;

import junit.framework.TestCase;

public class RegexItemFilterTest extends TestCase {

	public void testTitleWithNoPattern() throws Exception {
		
		RegexItemFilter filter = new RegexItemFilter();
		
		Item item = new Item();
		item.setTitle("foo");
		
		assertTrue(filter.evaluate(item));
	}
	
	public void textTitleWithMatchingPattern() throws Exception {
		
		RegexItemFilter filter = new RegexItemFilter();
		filter.setTitle("foo");
		
		Item item = new Item();
		item.setTitle("foo");
		
		assertTrue(filter.evaluate(item));		
	}
	
	public void testTitleWithNonMatchingPattern() throws Exception {
		
		RegexItemFilter filter = new RegexItemFilter();
		filter.setTitle("bar");
		
		Item item = new Item();
		item.setTitle("foo");
		
		assertFalse(filter.evaluate(item));
	}
}
