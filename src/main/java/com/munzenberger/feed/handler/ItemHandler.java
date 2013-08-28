package com.munzenberger.feed.handler;

import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.ui.MessageDispatcher;

public interface ItemHandler {

	public void process(Item item, MessageDispatcher dispatcher) throws ItemHandlerException;
}
