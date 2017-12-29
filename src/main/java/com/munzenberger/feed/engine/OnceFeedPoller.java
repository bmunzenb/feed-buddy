package com.munzenberger.feed.engine;

import java.io.File;

import com.munzenberger.feed.log.Logger;

public class OnceFeedPoller extends FeedPoller {

	public OnceFeedPoller(File file, String processed, Logger logger) {
		super(file, processed, logger);
	}

	@Override
	protected void prepareTimer() {
	}

	@Override
	protected void scheduleProcessor(FeedProcessor processor, long period) {
		processor.run();
	}

	@Override
	protected void scheduleConfigurationListener() {
	}
}
