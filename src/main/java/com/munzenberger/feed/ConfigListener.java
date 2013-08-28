package com.munzenberger.feed;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.munzenberger.feed.ui.MessageDispatcher;

public class ConfigListener extends TimerTask {

	private final File file;
	private final long lastModified;
	private final Timer timer;
	private final MessageDispatcher dispatcher;
	
	public ConfigListener(File file, Timer timer, MessageDispatcher dispatcher) {
		this.file = file;
		this.lastModified = file.lastModified();
		this.timer = timer;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void run() {
		if (lastModified != file.lastModified()) {
			timer.cancel();
			try {
				App.scheduleFeeds(file, dispatcher);
			}
			catch (Exception e) {
				dispatcher.error("Failed to schedule feeds", e);
			}
		}
	}
}
