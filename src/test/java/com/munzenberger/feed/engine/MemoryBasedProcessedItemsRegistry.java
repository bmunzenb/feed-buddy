package com.munzenberger.feed.engine;

import java.util.HashSet;
import java.util.Set;

import com.munzenberger.feed.parser.rss.Item;

public class MemoryBasedProcessedItemsRegistry implements ProcessedItemsRegistry {

	private final Set<Object> items = new HashSet<Object>();
	
	public void add(Item item) throws ProcessedItemsRegistryException {
		items.add(item.getGuid());
	}

	public boolean contains(Item item) {
		return items.contains(item.getGuid());
	}
}
