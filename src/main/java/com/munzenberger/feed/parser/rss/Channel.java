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
package com.munzenberger.feed.parser.rss;

import java.util.LinkedList;
import java.util.List;

public class Channel {

	private String title;
	
	private final List<Item> items = new LinkedList<>();

	public String getTitle() {
		return getTitle("");
	}

	public String getTitle(String defaultValue) {
		if (title == null || title.trim().isEmpty()) {
			return defaultValue;
		} else {
			return title;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Item> getItems() {
		return items;
	}
	
	public void addItem(Item i) {
		i.setChannel(this);
		items.add(i);
	}
}
