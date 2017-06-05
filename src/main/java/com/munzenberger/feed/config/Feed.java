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

public class Feed {

	private String url;

	private long period = 0;
	
	private String type = "rss";

	private final List<Handler> handlers = new LinkedList<Handler>();
	
	private final List<Filter> filters = new LinkedList<Filter>();
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Handler> getHandlers() {
		return handlers;
	}
	
	public void addHandler(Handler h) {
		handlers.add(h);
	}
	
	public List<Filter> getFilters() {
		return filters;
	}
	
	public void addFilter(Filter f) {
		filters.add(f);
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
