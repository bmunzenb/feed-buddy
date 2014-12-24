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
