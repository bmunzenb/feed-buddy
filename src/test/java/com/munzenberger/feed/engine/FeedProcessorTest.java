package com.munzenberger.feed.engine;

import java.util.LinkedList;
import java.util.List;

import com.munzenberger.feed.filter.ItemFilter;
import com.munzenberger.feed.filter.ItemFilterException;
import com.munzenberger.feed.parser.rss.Item;

import junit.framework.TestCase;

public class FeedProcessorTest extends TestCase {
	
	private final ItemFilter trueFilter = new ItemFilter() {
		@Override
		public boolean evaluate(Item item) throws ItemFilterException {
			return true;
		}
	};
	
	private final ItemFilter falseFilter = new ItemFilter() {
		@Override
		public boolean evaluate(Item item) throws ItemFilterException {
			return false;
		}		
	};
	
	public void testProcessFiltersTrue() throws Exception {
		
		List<ItemFilter> filters = new LinkedList<ItemFilter>();
		filters.add(trueFilter);
		filters.add(trueFilter);
		filters.add(trueFilter);
		
		FeedProcessor processor = new FeedProcessor(null, filters, null, null, null, null);
		
		assertTrue(processor.evaluateFilters(null));
	}
	
	public void testProcessFiltersFalse() throws Exception {
		
		List<ItemFilter> filters = new LinkedList<ItemFilter>();
		filters.add(trueFilter);
		filters.add(trueFilter);
		filters.add(falseFilter);
		
		FeedProcessor processor = new FeedProcessor(null, filters, null, null, null, null);
		
		assertFalse(processor.evaluateFilters(null));
	}
}
