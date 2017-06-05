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
package com.munzenberger.feed.engine;

import java.util.LinkedList;
import java.util.List;

import com.munzenberger.feed.filter.ItemFilter;
import com.munzenberger.feed.filter.ItemFilterException;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.log.DefaultLogger;
import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

import junit.framework.TestCase;

public class FeedProcessorTest extends TestCase {

	private static final Logger logger = new DefaultLogger();

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

	public void testProcessFilters() throws Exception {

		List<ItemFilter> filters = new LinkedList<ItemFilter>();

		FeedProcessor processor = new FeedProcessor(null, filters, null, null, null, null);

		assertTrue(processor.evaluateFilters(null, logger));
	}

	public void testProcessFiltersTrue() throws Exception {

		List<ItemFilter> filters = new LinkedList<ItemFilter>();
		filters.add(trueFilter);
		filters.add(trueFilter);
		filters.add(trueFilter);

		FeedProcessor processor = new FeedProcessor(null, filters, null, null, null, null);

		assertTrue(processor.evaluateFilters(null, logger));
	}

	public void testProcessFiltersFalse() throws Exception {

		List<ItemFilter> filters = new LinkedList<ItemFilter>();
		filters.add(trueFilter);
		filters.add(trueFilter);
		filters.add(falseFilter);

		FeedProcessor processor = new FeedProcessor(null, filters, null, null, null, null);

		assertFalse(processor.evaluateFilters(null, logger));
	}

	public void testExecuteHandlersWithNoHandlers() throws Exception {

		List<ItemHandler> handlers = new LinkedList<ItemHandler>();

		FeedProcessor processor = new FeedProcessor(null, null, handlers, null, null, null);

		assertTrue(processor.executeHandlers(null, logger));
	}
}
