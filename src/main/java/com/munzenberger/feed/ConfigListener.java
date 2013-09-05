package com.munzenberger.feed;

import java.io.File;
import java.util.TimerTask;

import com.munzenberger.feed.engine.FeedPoller;
import com.munzenberger.feed.ui.MessageDispatcher;

public class ConfigListener extends TimerTask {

	private final File file;
	private final long lastModified;
	private final FeedPoller poller;
	private final MessageDispatcher dispatcher;
	
	public ConfigListener(File file, FeedPoller poller, MessageDispatcher dispatcher) {
		this.file = file;
		this.lastModified = file.lastModified();
		this.poller = poller;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void run() {
		if (lastModified != file.lastModified()) {
			try {
				poller.start();
			}
			catch (Exception e) {
				dispatcher.error("Failed to schedule feeds", e);
			}
		}
	}
}
