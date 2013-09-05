package com.munzenberger.feed.handler;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

public class NoOp implements ItemHandler {

	public void process(Item item, Logger logger) throws ItemHandlerException {
	}
}
