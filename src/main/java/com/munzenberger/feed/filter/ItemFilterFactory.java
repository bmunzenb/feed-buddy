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
