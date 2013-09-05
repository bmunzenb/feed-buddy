package com.munzenberger.feed;

import java.io.File;
import java.util.TimerTask;

import com.munzenberger.feed.engine.FeedPoller;
import com.munzenberger.feed.log.Logger;

public class ConfigListener extends TimerTask {

	private final File file;
	private final long lastModified;
	private final FeedPoller poller;
	private final Logger logger;
	
	public ConfigListener(File file, FeedPoller poller, Logger logger) {
		this.file = file;
		this.lastModified = file.lastModified();
		this.poller = poller;
		this.logger = logger;
	}
	
	@Override
	public void run() {
		if (lastModified != file.lastModified()) {
			try {
				poller.start();
			}
			catch (Exception e) {
				logger.error("Failed to schedule feeds", e);
			}
		}
	}
}
