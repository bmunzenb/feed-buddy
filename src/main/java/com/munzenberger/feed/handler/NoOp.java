package com.munzenberger.feed.handler;

import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.ui.MessageDispatcher;

public class NoOp implements ItemHandler {

	public void process(Item item, MessageDispatcher dispatcher) throws ItemHandlerException {
	}
}
