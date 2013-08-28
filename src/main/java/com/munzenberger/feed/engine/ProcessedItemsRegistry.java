package com.munzenberger.feed.engine;

import com.munzenberger.feed.parser.rss.Item;


public interface ProcessedItemsRegistry {

	public boolean contains(Item item);
	
	public void add(Item item) throws ProcessedItemsRegistryException;
}
