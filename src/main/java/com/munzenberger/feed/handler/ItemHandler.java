package com.munzenberger.feed.handler;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

public interface ItemHandler {

	public void process(Item item, Logger logger) throws ItemHandlerException;
}
