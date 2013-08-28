package com.munzenberger.feed.parser.atom;

import java.util.LinkedList;
import java.util.List;

public class Atom {

	private String title;
	
	private final List<Entry> entries = new LinkedList<Entry>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Entry> getEntries() {
		return entries;
	}
	
	public void addEntry(Entry e) {
		entries.add(e);
	}
}
