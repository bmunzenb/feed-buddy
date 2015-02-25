package com.munzenberger.feed.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerFactoryException;
import com.munzenberger.feed.log.Logger;

public class NoopFeedPoller extends FeedPoller {

	public NoopFeedPoller(File file, Logger logger) {
		super(file, logger);
	}

	@Override
	protected void prepareTimer() {
	}

	@Override
	protected List<ItemHandler> getHandlers(Feed feed) throws ItemHandlerFactoryException {
		return new ArrayList<ItemHandler>(0);
	}

	@Override
	protected void scheduleProcessor(FeedProcessor processor, long period) {
		processor.run();
	}

	@Override
	protected void scheduleConfigurationListener() {
	}
}
