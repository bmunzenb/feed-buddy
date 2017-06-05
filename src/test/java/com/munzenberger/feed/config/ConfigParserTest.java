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
package com.munzenberger.feed.config;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ConfigParserTest extends TestCase {

	public void testParse() throws Exception {
		
		InputStream in = ConfigParserTest.class.getResourceAsStream("feeds.xml");
		
		Feeds config = ConfigParser.parse(in);
		
		assertNotNull(config);
		
		List<Feed> feeds = config.getFeeds();
		
		assertNotNull(feeds);
		assertEquals(1, feeds.size());
		
		Feed feed = feeds.get(0);
		
		List<Filter> filters = feed.getFilters();
		
		assertNotNull(filters);
		assertEquals(1, filters.size());
		
		Filter filter = filters.get(0);
		Map<String, String> properties = filter.getProperties();
		
		assertNotNull(properties);
		assertEquals(1, properties.size());
		assertEquals("bar", properties.get("foo"));
		
		List<Handler> handlers = feed.getHandlers();
		
		assertNotNull(handlers);
		assertEquals(1, handlers.size());
		
		Handler handler = handlers.get(0);
		properties = handler.getProperties();
		
		assertNotNull(properties);
		assertEquals(1, properties.size());
		assertEquals("conquer", properties.get("command"));
	}
}
