package com.munzenberger.feed.filter;

import com.munzenberger.feed.parser.rss.Item;

public class RegexItemFilter implements ItemFilter {

	private String title = null;
	private String description = null;
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean evaluate(Item item) throws ItemFilterException {
		
		return matches(title, item.getTitle()) && matches(description, item.getDescription());
	}
	
	protected boolean matches(String pattern, String text) {
		
		if (pattern == null) {
			return true;
		}
		else if (text == null) {
			text = "";
		}
		
		return text.matches(pattern);
	}
}
