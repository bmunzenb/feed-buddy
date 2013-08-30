package com.munzenberger.feed.engine;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.config.Handler;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerException;
import com.munzenberger.feed.handler.ItemHandlerFactory;
import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.ParserFactory;
import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.ui.MessageDispatcher;

public class FeedProcessor implements Runnable {

	private URL url;
	private List<ItemHandler> handlers = new LinkedList<ItemHandler>();
	private ProcessedItemsRegistry processed;
	private MessageDispatcher dispatcher;
	private Parser parser;
	
	public FeedProcessor(Feed config, MessageDispatcher dispatcher, boolean noop) throws FeedProcessorException {
		try {
			init(config, dispatcher, noop, new ProcessedItemsRegistryImpl(config));
		}
		catch (ProcessedItemsRegistryException e) {
			throw new FeedProcessorException("Could not initialize feed processor", e);
		}
	}
	
	public FeedProcessor(Feed config, MessageDispatcher dispatcher, boolean noop, ProcessedItemsRegistry registry) throws FeedProcessorException {
		init(config, dispatcher, noop, registry);
	}
	
	private final void init(Feed config, MessageDispatcher dispatcher, boolean noop, ProcessedItemsRegistry registry) throws FeedProcessorException {
		try {
			this.dispatcher = dispatcher;
			this.url = new URL(config.getUrl());
			this.processed = registry;
			this.parser = ParserFactory.getParser(config.getType());
			
			if (noop) {
				Handler h = new Handler();
				h.setClazz("com.munzenberger.feed.handler.NoOp");
				ItemHandler handler = ItemHandlerFactory.getInstance(h);
				handlers.add(handler);
			}
			else {
				for (Handler h : config.getHandlers()) {
					ItemHandler handler = ItemHandlerFactory.getInstance(h);
					handlers.add(handler);
				}
			}
		}
		catch (Exception e) {
			throw new FeedProcessorException("Could not initialize feed processor", e);
		}
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
