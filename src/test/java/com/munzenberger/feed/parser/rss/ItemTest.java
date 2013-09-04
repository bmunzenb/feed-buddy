package com.munzenberger.feed.parser.rss;

import junit.framework.TestCase;

public class ItemTest extends TestCase {

	public void testDescriptionEntityEncoded() {
		
		Item item = new Item();
		item.setDescription("Hello");
		
		String encoded = item.getDescriptionEntityEncoded();
		assertEquals("Hello", encoded);
		
		item.setDescription("\u00ae");
		encoded = item.getDescriptionEntityEncoded();
		assertEquals("&#0174;", encoded);
	}
}
