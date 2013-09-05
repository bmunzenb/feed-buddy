package com.munzenberger.feed.engine;

import java.net.URL;
import java.util.List;
import java.util.TimerTask;

import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerException;
import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.parser.rss.RSS;

public class FeedProcessor implements Runnable {

	private final URL url;
	private final List<ItemHandler> handlers;
	private final ProcessedItemsRegistry registry;
	private final Parser parser;
	private final Logger logger;

	public FeedProcessor(URL url, List<ItemHandler> handlers, ProcessedItemsRegistry registry, Parser parser, Logger logger) {
		this.url = url;
		this.handlers = handlers;
		this.registry = registry;
		this.parser = parser;
		this.logger = logger;
	}
	
	public void run() {
		try {
			RSS rss = parser.parse(url);
			process(rss);
		} 
		catch (Exception e) {
			logger.error("Exception while processing feed " + url, e);
		}
	}
	
	protected void process(RSS rss) throws FeedProcessorException {
		for (Channel c : rss.getChannels()) {
			process(c);
		}
	}
	
	protected void process(Channel channel) throws FeedProcessorException {
		logger.info("Scanning " + channel.getTitle() + "...");
		for (Item i : channel.getItems()) {
			process(i);
		}
	}
	
	protected void process(Item item) throws FeedProcessorException {
		if (!registry.contains(item)) {
			logger.info("Processing " + item.getTitle() + "...");
			
			boolean success = false;

			for (ItemHandler h : handlers) {
				try {
					h.process(item, logger);
					success = true;
				}
				catch (ItemHandlerException e) {
					logger.error("Failed to process item: " + item.getGuid(), e);
				}
			}

			if (success) {
				try {
					registry.add(item);
				}
				catch (ProcessedItemsRegistryException e) {
					throw new FeedProcessorException("Failed to mark item as processed: " + item.getGuid(), e);
				}
			}
		}
	}
	
	public TimerTask getTimerTask() {
		final FeedProcessor f = this;
		return new TimerTask() {
			@Override
			public void run() {
				f.run();
			}
		};
	}
}
