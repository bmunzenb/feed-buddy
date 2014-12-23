package com.munzenberger.feed.handler;

import java.util.HashMap;
import java.util.Map;

import com.munzenberger.feed.config.Handler;
import com.munzenberger.feed.util.PropertySetter;

public class ItemHandlerFactory {

	private static final Map<String, ItemHandler> handlers = new HashMap<String, ItemHandler>();
	
	public static ItemHandler getInstance(Handler h) throws ItemHandlerFactoryException {
		if (h.getRef() != null) {
			ItemHandler handler = handlers.get(h.getRef());
			if (handler == null) {
				throw new ItemHandlerFactoryException("No item handler found for ref " + h.getRef());
			} else {
				return handler;
			}
		}
		
		ItemHandler handler = newHandler(h);
		handlers.put(h.getName(), handler);
		return handler;
	}
	
	@SuppressWarnings("unchecked")
	protected static ItemHandler newHandler(Handler h) throws ItemHandlerFactoryException {
		try {
			Class<ItemHandler> clazz = (Class<ItemHandler>) Class.forName(h.getClazz());
			ItemHandler handler = clazz.newInstance();
			PropertySetter.setProperties(clazz, handler, h);
			
			return handler;
		}
		catch (Exception e) {
			throw new ItemHandlerFactoryException("Could not create handler " + h.getClazz(), e);
		}
	}
}
