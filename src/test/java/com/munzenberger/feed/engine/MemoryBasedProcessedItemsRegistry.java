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
package com.munzenberger.feed.engine;

import java.util.HashSet;
import java.util.Set;

import com.munzenberger.feed.parser.rss.Item;

public class MemoryBasedProcessedItemsRegistry implements ProcessedItemsRegistry {

	private final Set<Object> items = new HashSet<Object>();
	
	public void add(Item item) throws ProcessedItemsRegistryException {
		items.add(item.getGuid());
	}

	public boolean contains(Item item) {
		return items.contains(item.getGuid());
	}
}
