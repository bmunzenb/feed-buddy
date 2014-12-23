package com.munzenberger.feed.handler;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

public abstract class FilterableItemHandler implements ItemHandler {

	private String titleRegex = null;
	private String descriptionRegex = null;
	
	public void setTitleRegex(String title) {
		this.titleRegex = title;
	}

	public void setDescriptionRegex(String description) {
		this.descriptionRegex = description;
	}

	@Override
	public void process(Item item, Logger logger) throws ItemHandlerException {
		
		if (matches(titleRegex, item.getTitle()) && matches(descriptionRegex, item.getDescription())) {
			processMatchedItem(item, logger);
		}
		else {
			processUnmatchedItem(item, logger);
		}
	}
	
	protected void processMatchedItem(Item item, Logger logger) throws ItemHandlerException {
		// child classes will override
	}
	
	protected void processUnmatchedItem(Item item, Logger logger) throws ItemHandlerException {
		// child classes will override
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
