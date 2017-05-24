package com.munzenberger.feed.engine;

import java.io.File;
import java.util.TimerTask;

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
		try {
			if (lastModified != file.lastModified()) {
				logger.log("Detected configuration change, restarting feed poller...");
				poller.start();
			}
		}
		catch (Throwable t) {
			logger.log("ConfigListener failure", t);
		}
	}
}
