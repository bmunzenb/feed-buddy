package com.munzenberger.feed.handler;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.munzenberger.feed.config.Handler;
import com.munzenberger.feed.config.Property;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerException;
import com.munzenberger.feed.handler.ItemHandlerFactory;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.ui.MessageDispatcher;

public class HandlerFactoryTest extends TestCase {

	public void testNewHandlerProperties() throws Exception {
		
		Handler h = new Handler();
		h.setClazz(MockItemHandler.class.getName());
		h.addProperty(new Property("poop", "value1"));
		h.addProperty(new Property("dick", "value2"));
		
		MockItemHandler handler = (MockItemHandler) ItemHandlerFactory.newHandler(h);
		handler.assertValues("value1", "value2");
	}
}

class MockItemHandler implements ItemHandler {

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
	
	@Override
	public void process(Item item, MessageDispatcher dispatcher) throws ItemHandlerException {
		throw new UnsupportedOperationException("Method not implemented");
	}
}
