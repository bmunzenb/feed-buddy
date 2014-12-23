package com.munzenberger.feed.filter;

import com.munzenberger.feed.parser.rss.Item;

import junit.framework.TestCase;

public class RegexItemFilterTest extends TestCase {

	public void testTitleWithNoPattern() throws Exception {
		
		RegexItemFilter filter = new RegexItemFilter();
		
		Item item = new Item();
		item.setTitle("foo");
		
		assertTrue(filter.filter(item));
	}
	
	public void textTitleWithMatchingPattern() throws Exception {
		
		RegexItemFilter filter = new RegexItemFilter();
		filter.setTitle("foo");
		
		Item item = new Item();
		item.setTitle("foo");
		
		assertTrue(filter.filter(item));		
	}
	
	public void testTitleWithNonMatchingPattern() throws Exception {
		
		RegexItemFilter filter = new RegexItemFilter();
		filter.setTitle("bar");
		
		Item item = new Item();
		item.setTitle("foo");
		
		assertFalse(filter.filter(item));
	}
}
