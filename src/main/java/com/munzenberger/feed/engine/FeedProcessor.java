package com.munzenberger.feed.engine;

import java.net.URL;
import java.util.List;
import java.util.TimerTask;

import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerException;
import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.ui.MessageDispatcher;

public class FeedProcessor implements Runnable {

	private final URL url;
	private final List<ItemHandler> handlers;
	private final ProcessedItemsRegistry processed;
	private final Parser parser;
	private final MessageDispatcher dispatcher;

	public FeedProcessor(URL url, List<ItemHandler> handlers, ProcessedItemsRegistry registry, Parser parser, MessageDispatcher dispatcher) {
		this.url = url;
		this.handlers = handlers;
		this.processed = registry;
		this.parser = parser;
		this.dispatcher = dispatcher;
	}
	
	public void run() {
		try {
			RSS rss = parser.parse(url);
			process(rss);
		} 
		catch (Exception e) {
			dispatcher.error("Exception while processing feed " + url, e);
		}
	}
	
	protected void process(RSS rss) throws FeedProcessorException {
		for (Channel c : rss.getChannels()) {
			process(c);
		}
	}
	
	protected void process(Channel channel) throws FeedProcessorException {
		dispatcher.info("Scanning " + channel.getTitle() + "...");
		for (Item i : channel.getItems()) {
			process(i);
		}
	}
	
	protected void process(Item item) throws FeedProcessorException {
		if (!processed.contains(item)) {
			dispatcher.info("Processing " + item.getTitle() + "...");
			
			boolean success = false;

			for (ItemHandler h : handlers) {
				try {
					h.process(item, dispatcher);
					success = true;
				}
				catch (ItemHandlerException e) {
					dispatcher.error("Failed to process item: " + item.getGuid(), e);
				}
			}

			if (success) {
				try {
					processed.add(item);
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
