package com.munzenberger.feed.engine;

import java.io.File;
import java.util.Timer;

import com.munzenberger.feed.ConfigListener;
import com.munzenberger.feed.config.ConfigParser;
import com.munzenberger.feed.config.ConfigParserException;
import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.config.Feeds;
import com.munzenberger.feed.ui.MessageDispatcher;

public class FeedPoller {

	private static final int FIVE_SECONDS_IN_MILLIS = 1000 * 5;
	private static final int ONE_MINUTE_IN_MILLIS = 60 * 1000;
	
	private Timer timer;
	private boolean noop = false;
	private MessageDispatcher dispatcher;

	public FeedPoller(MessageDispatcher messageDispatcher, boolean noop) {
		this.dispatcher = messageDispatcher;
		this.noop = noop;
	}
	
	public void stop() {
		if (this.timer != null) {
			this.timer.cancel();
		}
	}
	
	public void scheduleFeeds(File file) throws FeedProcessorException, ConfigParserException {
		
		stop();
		
		timer = new Timer();

		Feeds config = ConfigParser.parse(file);
		scheduleFeeds(config, timer, dispatcher, noop);

		if (!noop) {
			ConfigListener configListener = new ConfigListener(file, this, dispatcher);
			timer.schedule(configListener, 0, FIVE_SECONDS_IN_MILLIS);
		}
	}
	
	private static void scheduleFeeds(Feeds config, Timer timer, MessageDispatcher dispatcher, boolean noop) throws FeedProcessorException {
		
		dispatcher.info("Scheduling " + config.getFeeds().size() + " feed" + (config.getFeeds().size() != 1 ? "s" : "") + "...");
		
		for (Feed f : config.getFeeds()) {
			FeedProcessor p = new FeedProcessor(f, dispatcher, noop);
			
			if (noop) {
				p.run();
			}
			else {
				long period;
				
				// period is configured in minutes
				if (f.getPeriod() > 0) {
					period = f.getPeriod();
				} else {
					period = config.getPeriod();
				}
				
				period = period * ONE_MINUTE_IN_MILLIS; // convert to millis
				
				timer.schedule(p.getTimerTask(), 0, period);
			}
		}
	}
}
