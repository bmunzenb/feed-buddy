package com.munzenberger.feed.filter;

import com.munzenberger.feed.parser.rss.Item;

public interface ItemFilter {

	public boolean filter(Item item) throws ItemFilterException;
}
