package com.munzenberger.feed.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.munzenberger.feed.config.Handler;

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
			
			for (Map.Entry<String,String> e : h.getProperties().entrySet()) {
				String name = e.getKey();
				name = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
				
				Method setter = clazz.getMethod(name, String.class);
				setter.invoke(handler, e.getValue());
			}
			
			return handler;
		}
		catch (Exception e) {
			throw new ItemHandlerFactoryException("Could not create handler " + h.getClazz(), e);
		}
	}
}
