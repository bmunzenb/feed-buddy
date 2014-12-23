package com.munzenberger.feed.handler;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

import junit.framework.TestCase;

public class FilterableItemHandlerTest extends TestCase {

	private class FilterableItemHandlerImpl extends FilterableItemHandler {
		
		private int matched = 0;
		private int unmatched = 0;
		
		@Override
		protected void processMatchedItem(Item item, Logger logger) throws ItemHandlerException {
			matched++;
		}
		
		@Override
		protected void processUnmatchedItem(Item item, Logger logger) throws ItemHandlerException {
			unmatched++;
		}
	}
	
	public void testTitleWithNullPattern() throws Exception {
	
		FilterableItemHandlerImpl handler = new FilterableItemHandlerImpl();
		
		Item item = new Item();
		item.setTitle("foo");
		
		handler.process(item, null);
		
		assertEquals(1, handler.matched);
		assertEquals(0, handler.unmatched);
	}
	
	public void testTitleWithMatch() throws Exception {
		
		FilterableItemHandlerImpl handler = new FilterableItemHandlerImpl();
		handler.setTitleRegex("foo");
		
		Item item = new Item();
		item.setTitle("foo");
		
		handler.process(item, null);
		
		assertEquals(1, handler.matched);
		assertEquals(0, handler.unmatched);
	}
	
	public void testTitleWithoutMatch() throws Exception {
		
		FilterableItemHandlerImpl handler = new FilterableItemHandlerImpl();
		handler.setTitleRegex("foo");
		
		Item item = new Item();
		item.setTitle("bar");
		
		handler.process(item, null);
		
		assertEquals(0, handler.matched);
		assertEquals(1, handler.unmatched);
	}
}
