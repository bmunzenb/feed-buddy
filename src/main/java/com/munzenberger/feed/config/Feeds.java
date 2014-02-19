package com.munzenberger.feed.config;

import java.util.LinkedList;
import java.util.List;

import com.munzenberger.feed.handler.ItemHandlerFactory;
import com.munzenberger.feed.handler.ItemHandlerFactoryException;

public class Feeds {
	
	private final List<Feed> feeds = new LinkedList<Feed>();
	
	private long period = 60;  // default period of 60 minutes
	
	private String agent = "feed-buddy";

	public List<Feed> getFeeds() {
		return feeds;
	}

	public void addFeed(Feed f) {
		feeds.add(f);
	}
	
	public void addHandler(Handler h) throws ItemHandlerFactoryException {
		ItemHandlerFactory.getInstance(h);
	}
	
	public void setPeriod(long period) {
		this.period = period;
	}
	
	public long getPeriod() {
		return period;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getAgent() {
		return agent;
	}
}
