package com.munzenberger.feed.engine;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import com.munzenberger.feed.filter.ItemFilter;
import com.munzenberger.feed.filter.ItemFilterException;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerException;
import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.util.URLProcessor;

public class FeedProcessor implements Runnable {

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
	
	public void run() {
		try {
			InputStream in = URLProcessor.getInputStream(url);

			RSS rss = parser.parse(in);
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
			
			boolean shouldProcess = evaluateFilters(i);
			
			if (shouldProcess) {
				process(i);
			}
		}
	}
	
	protected void process(Item item) throws FeedProcessorException {
		
		if (!registry.contains(item)) {
			
			logger.info("Processing " + item.getTitle() + "...");
				
			boolean success = executeHandlers(item);

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
	
	protected boolean evaluateFilters(Item item) {
		
		boolean result = true;
		
		Iterator<ItemFilter> i = filters.iterator();
		
		while (result && i.hasNext()) {
			
			ItemFilter f = i.next();
			
			try {
				result = f.evaluate(item);
			}
			catch (ItemFilterException e) {
				logger.error("Filter failed to evaluate item: " + item.getGuid(), e);
				result = false;
			}
		}
		
		return result;
	}
	
	protected boolean executeHandlers(Item item) {
		
		boolean success = true;
		
		for (ItemHandler h : handlers) {
			
			try {
				h.process(item, logger);
			}
			catch (ItemHandlerException e) {
				logger.error("Handler failed to process item: " + item.getGuid(), e);
				success = false;
			}
		}
		
		return success;
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
