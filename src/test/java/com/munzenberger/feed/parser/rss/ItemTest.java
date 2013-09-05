package com.munzenberger.feed.parser.rss;

import com.munzenberger.feed.handler.SendEmail.MailItem;

import junit.framework.TestCase;

public class ItemTest extends TestCase {

	public void testDescriptionEntityEncoded() {
		
		Item item = new Item();
		item.setDescription("Hello");
		
		MailItem mailItem = new MailItem(item);
		
		String encoded = mailItem.getDescription();
		assertEquals("Hello", encoded);
		
		item.setDescription("\u00ae");
		encoded = mailItem.getDescription();
		assertEquals("&#0174;", encoded);
	}
}
