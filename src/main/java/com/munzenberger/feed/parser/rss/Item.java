package com.munzenberger.feed.parser.rss;

import java.util.LinkedList;
import java.util.List;

public class Item {

	private String title;
	
	private String link;
	
	private String guid;
	
	private String description;
	
	private String author;
	
	private String pubDate;
	
	private Channel channel;

	private final List<Enclosure> enclosures = new LinkedList<Enclosure>();
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getGuid() {
		if (guid == null || guid.trim().length() == 0) {
			return link;
		} else {
			return guid;
		}
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public List<Enclosure> getEnclosures() {
		return enclosures;
	}
	
	public void addEnclosure(Enclosure e) {
		enclosures.add(e);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}