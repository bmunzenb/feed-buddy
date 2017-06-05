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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerFactoryException;
import com.munzenberger.feed.log.Logger;

public class NoopFeedPoller extends FeedPoller {

	public NoopFeedPoller(File file, String processed, Logger logger) {
		super(file, processed, logger);
	}

	@Override
	protected void prepareTimer() {
	}

	@Override
	protected List<ItemHandler> getHandlers(Feed feed) throws ItemHandlerFactoryException {
		return new ArrayList<ItemHandler>(0);
	}

	@Override
	protected void scheduleProcessor(FeedProcessor processor, long period) {
		processor.run();
	}

	@Override
	protected void scheduleConfigurationListener() {
	}
}
