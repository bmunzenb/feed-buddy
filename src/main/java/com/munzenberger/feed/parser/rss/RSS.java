package com.munzenberger.feed.parser.rss;

import java.util.LinkedList;
import java.util.List;

public class RSS {

	private String version;
	
	private final List<Channel> channels = new LinkedList<Channel>();

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void addChannel(Channel c) {
		channels.add(c);
	}
}
