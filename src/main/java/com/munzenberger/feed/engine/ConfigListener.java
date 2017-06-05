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
import java.util.TimerTask;

import com.munzenberger.feed.log.Logger;

public class ConfigListener extends TimerTask {

	private final File file;
	private final long lastModified;
	private final FeedPoller poller;
	private final Logger logger;

	public ConfigListener(File file, FeedPoller poller, Logger logger) {
		this.file = file;
		this.lastModified = file.lastModified();
		this.poller = poller;
		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			if (lastModified != file.lastModified()) {
				logger.log("Detected configuration change, restarting feed poller...");
				poller.start();
			}
		}
		catch (Throwable t) {
			logger.log("ConfigListener failure", t);
		}
	}
}
