package com.munzenberger.feed.parser.rss;

import java.util.LinkedList;
import java.util.List;

public class Channel {

	private String title;
	
	private final List<Item> items = new LinkedList<Item>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Item> getItems() {
		return items;
	}
	
	public void addItem(Item i) {
		i.setChannel(this);
		items.add(i);
	}
}
