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

import com.munzenberger.feed.config.ConfigParser;
import com.munzenberger.feed.config.ConfigParserException;
import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.config.Feeds;
import com.munzenberger.feed.config.Filter;
import com.munzenberger.feed.config.Handler;
import com.munzenberger.feed.filter.ItemFilter;
import com.munzenberger.feed.filter.ItemFilterFactory;
import com.munzenberger.feed.filter.ItemFilterFactoryException;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerFactory;
import com.munzenberger.feed.handler.ItemHandlerFactoryException;
import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.ParserFactory;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

public class FeedPoller {

	private static final int FIVE_SECONDS_IN_MILLIS = 5 * 1000;
	private static final int ONE_MINUTE_IN_MILLIS = 60 * 1000;

	private final File file;
	private final String processed;
	private final Logger logger;

	private Timer timer;

	public FeedPoller(File file, String processed, Logger logger) {
		this.file = file;
		this.processed = processed;
		this.logger = logger;
	}

	public void start() throws ConfigParserException, FeedProcessorException {

		stop();

		Feeds config = ConfigParser.parse(file);

		prepareTimer();

		scheduleFeeds(config);

		scheduleConfigurationListener();
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
		}
	}

	protected void prepareTimer() {
		timer = new Timer();
	}

	protected void scheduleFeeds(Feeds config) throws FeedProcessorException {

		logger.log("Scheduling " + config.getFeeds().size() + " feed" + (config.getFeeds().size() != 1 ? "s" : "") + "...");

		for (Feed feed : config.getFeeds()) {
			FeedProcessor processor = getFeedProcessor(feed);

			// period is configured in minutes
			long period = config.getPeriod();
			if (feed.getPeriod() > 0) {
				period = feed.getPeriod();
			}

			period = period * ONE_MINUTE_IN_MILLIS; // convert to millis

			scheduleProcessor(processor, period);
		}
	}

	protected FeedProcessor getFeedProcessor(Feed feed) throws FeedProcessorException {
		try {
			URL url = new URL(feed.getUrl());
			String userAgent = feed.getUserAgent();
			List<ItemFilter> filters = getFilters(feed);
			List<ItemHandler> handlers = getHandlers(feed);
			ProcessedItemsRegistry registry = new FileBasedProcessedItemsRegistry(processed, feed);
			Parser parser = ParserFactory.getParser(feed.getType());

			return new FeedProcessor(url, userAgent, filters, handlers, registry, parser, logger);
		}
		catch (Exception e) {
			throw new FeedProcessorException("Could not initialize feed processor", e);
		}
	}

	protected List<ItemFilter> getFilters(Feed feed) throws ItemFilterFactoryException {
		List<ItemFilter> filters = new LinkedList<ItemFilter>();
		for (Filter f : feed.getFilters()) {
			ItemFilter filter = ItemFilterFactory.getInstance(f);
			filters.add(filter);
		}
		return filters;
	}

	protected List<ItemHandler> getHandlers(Feed feed) throws ItemHandlerFactoryException {
		List<ItemHandler> handlers = new LinkedList<ItemHandler>();
		for (Handler h : feed.getHandlers()) {
			ItemHandler handler = ItemHandlerFactory.getInstance(h);
			handlers.add(handler);
		}
		return handlers;
	}

	protected void scheduleProcessor(FeedProcessor processor, long period) {
		timer.schedule(processor, 0, period);
	}

	protected void scheduleConfigurationListener() {
		ConfigListener configListener = new ConfigListener(file, this, logger);
		timer.schedule(configListener, FIVE_SECONDS_IN_MILLIS, FIVE_SECONDS_IN_MILLIS);
	}
}
