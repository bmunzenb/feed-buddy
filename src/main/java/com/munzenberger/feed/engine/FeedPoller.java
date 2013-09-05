package com.munzenberger.feed.engine;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import com.munzenberger.feed.ConfigListener;
import com.munzenberger.feed.config.ConfigParser;
import com.munzenberger.feed.config.ConfigParserException;
import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.config.Feeds;
import com.munzenberger.feed.config.Handler;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerFactory;
import com.munzenberger.feed.handler.ItemHandlerFactoryException;
import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.ParserFactory;

public class FeedPoller {

	private static final int FIVE_SECONDS_IN_MILLIS = 1000 * 5;
	private static final int ONE_MINUTE_IN_MILLIS = 60 * 1000;
	
	private final File file;
	private final Logger logger;

	private Timer timer;
	
	public FeedPoller(File file, Logger logger) {
		this.file = file;
		this.logger = logger;
	}
	
	public void start() throws ConfigParserException, FeedProcessorException {
		
		stop();
		
		prepareTimer();
		
		Feeds config = ConfigParser.parse(file);
		scheduleFeeds(config);
	}
	
	public void stop() {
		if (this.timer != null) {
			this.timer.cancel();
		}
	}
	
	protected void prepareTimer() {
		timer = new Timer();
	}
	
	protected void scheduleFeeds(Feeds config) throws FeedProcessorException {
		
		logger.info("Scheduling " + config.getFeeds().size() + " feed" + (config.getFeeds().size() != 1 ? "s" : "") + "...");
		
		for (Feed feed : config.getFeeds()) {
			FeedProcessor processor = getFeedProcessor(feed);
			
			long period;
			
			// period is configured in minutes
			if (feed.getPeriod() > 0) {
				period = feed.getPeriod();
			} else {
				period = config.getPeriod();
			}
			
			period = period * ONE_MINUTE_IN_MILLIS; // convert to millis
			
			scheduleProcessor(processor, period);
		}
		
		scheduleConfigurationListener();
	}
	
	protected FeedProcessor getFeedProcessor(Feed feed) throws FeedProcessorException {
		try {
			URL url = new URL(feed.getUrl());
			List<ItemHandler> handlers = getHandlers(feed);
			ProcessedItemsRegistry registry = new FileBasedProcessedItemsRegistry(feed);
			Parser parser = ParserFactory.getParser(feed.getType());
			
			return new FeedProcessor(url, handlers, registry, parser, logger);
		}
		catch (Exception e) {
			throw new FeedProcessorException("Could not initialize feed processor", e);
		}
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
		timer.schedule(processor.getTimerTask(), 0, period);
	}
	
	protected void scheduleConfigurationListener() {
		ConfigListener configListener = new ConfigListener(file, this, logger);
		timer.schedule(configListener, FIVE_SECONDS_IN_MILLIS, FIVE_SECONDS_IN_MILLIS);
	}
}
