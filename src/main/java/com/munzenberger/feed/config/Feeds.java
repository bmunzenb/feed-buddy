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
package com.munzenberger.feed.config;

import java.util.LinkedList;
import java.util.List;

import com.munzenberger.feed.handler.ItemHandlerFactory;
import com.munzenberger.feed.handler.ItemHandlerFactoryException;

public class Feeds {
	
	private final List<Feed> feeds = new LinkedList<Feed>();
	
	private long period = 60;  // default period of 60 minutes

	public List<Feed> getFeeds() {
		return feeds;
	}

	public void addFeed(Feed f) {
		feeds.add(f);
	}
	
	public void addHandler(Handler h) throws ItemHandlerFactoryException {
		ItemHandlerFactory.getInstance(h);
	}
	
	public void setPeriod(long period) {
		this.period = period;
	}
	
	public long getPeriod() {
		return period;
	}
}
