package com.munzenberger.feed.handler;

import junit.framework.TestCase;

import com.munzenberger.feed.config.Handler;
import com.munzenberger.feed.config.Property;

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
