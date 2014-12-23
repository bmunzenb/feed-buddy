package com.munzenberger.feed.filter;

import com.munzenberger.feed.config.Filter;
import com.munzenberger.feed.util.PropertySetter;

public class ItemFilterFactory {

	public static ItemFilter getInstance(Filter f) throws ItemFilterFactoryException {
		return newFilter(f);
	}
	
	@SuppressWarnings("unchecked")
	protected static ItemFilter newFilter(Filter f) throws ItemFilterFactoryException {
		try {
			Class<ItemFilter> clazz = (Class<ItemFilter>) Class.forName(f.getClazz());
			ItemFilter filter = clazz.newInstance();
			PropertySetter.setProperties(clazz, filter, f);
			
			return filter;
		}
		catch (Exception e) {
			throw new ItemFilterFactoryException("Could not create filter " + f.getClazz(), e);
		}
	}
}
