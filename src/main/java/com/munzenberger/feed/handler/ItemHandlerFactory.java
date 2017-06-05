/*
 * Copyright 2017 Brian Munzenberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
