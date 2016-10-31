package com.munzenberger.feed.filter;

import java.util.List;

import com.munzenberger.feed.parser.rss.Item;

public class RegexItemFilter implements ItemFilter {

	private String title = null;
	private String description = null;
	private String category = null;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public boolean evaluate(Item item) throws ItemFilterException {

		return matches(title, item.getTitle()) && matches(description, item.getDescription()) && matches(category, item.getCategories());
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

	protected boolean matches(String pattern, List<String> texts) {

		if (pattern == null) {
			return true;
		}

		for (String s : texts) {
			// only one category needs to match
			if (s.matches(pattern)) {
				return true;
			}
		}

		return false;
	}
}
