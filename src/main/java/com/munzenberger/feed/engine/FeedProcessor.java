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

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import com.munzenberger.feed.filter.ItemFilter;
import com.munzenberger.feed.filter.ItemFilterException;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerException;
import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.log.TaggedLogger;
import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.parser.rss.RSS;

public class FeedProcessor extends TimerTask {

	private final URL url;
	private final List<ItemFilter> filters;
	private final List<ItemHandler> handlers;
	private final ProcessedItemsRegistry registry;
	private final Parser parser;
	private final Logger logger;

	public FeedProcessor(URL url, List<ItemFilter> filters, List<ItemHandler> handlers, ProcessedItemsRegistry registry, Parser parser, Logger logger) {
		this.url = url;
		this.filters = filters;
		this.handlers = handlers;
		this.registry = registry;
		this.parser = parser;
		this.logger = logger;
	}

	@Override
	public void run() {
		try {

			RSS rss = parser.parse(url);
			process(rss);
		} 
		catch (Throwable e) {
			logger.log("Failure while processing feed " + url, e);
		}
	}

	protected void process(RSS rss) throws FeedProcessorException {
		for (Channel c : rss.getChannels()) {
			process(c);
		}
	}

	protected void process(Channel channel) throws FeedProcessorException {

		Logger localLogger = new TaggedLogger(channel.getTitle(url.toString()), logger);

		localLogger.log("Scanning...");

		for (Item i : channel.getItems()) {

			boolean shouldProcess = evaluateFilters(i, localLogger);

			if (shouldProcess) {
				process(i, localLogger);
			}
		}
	}

	protected void process(Item item, Logger localLogger) throws FeedProcessorException {

		if (!registry.contains(item)) {

			localLogger.log("Processing " + item + "...");

			boolean success = executeHandlers(item, localLogger);

			if (success) {
				try {
					registry.add(item);
				}
				catch (ProcessedItemsRegistryException e) {
					throw new FeedProcessorException("Failed to mark item as processed: " + item, e);
				}
			}
		}
	}

	protected boolean evaluateFilters(Item item, Logger localLogger) {

		boolean result = true;

		Iterator<ItemFilter> i = filters.iterator();

		while (result && i.hasNext()) {

			ItemFilter f = i.next();

			try {
				result = f.evaluate(item);
			}
			catch (ItemFilterException e) {
				localLogger.log("Filter failed to evaluate item: " + item, e);
				result = false;
			}
		}

		return result;
	}

	protected boolean executeHandlers(Item item, Logger localLogger) {

		boolean success = true;

		for (ItemHandler h : handlers) {

			try {
				h.process(item, localLogger);
			}
			catch (ItemHandlerException e) {
				localLogger.log("Handler failed to process item: " + item, e);
				success = false;
			}
		}

		return success;
	}
}
