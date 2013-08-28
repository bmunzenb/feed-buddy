package com.munzenberger.feed.config;

import java.util.LinkedList;
import java.util.List;

public class Feed {

	private String url;

	private long period = 0;
	
	private String type = "rss";

	private final List<Handler> handlers = new LinkedList<Handler>();
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Handler> getHandlers() {
		return handlers;
	}
	
	public void addHandler(Handler h) {
		handlers.add(h);
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
