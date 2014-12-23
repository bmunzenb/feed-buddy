package com.munzenberger.feed.handler;

import junit.framework.Assert;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

public class MockItemHandler implements ItemHandler {

	private String name1;
	
	private String name2;
	
	public void setPoop(String name1) {
		this.name1 = name1;
	}

	public void setDick(String name2) {
		this.name2 = name2;
	}

	public void assertValues(String value1, String value2) {
		Assert.assertEquals(value1, this.name1);
		Assert.assertEquals(value2, this.name2);
	}
	
	public void process(Item item, Logger logger) throws ItemHandlerException {
		throw new UnsupportedOperationException("Method not implemented");
	}
}
