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
package com.munzenberger.feed;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.munzenberger.feed.engine.FeedPoller;
import com.munzenberger.feed.engine.NoopFeedPoller;
import com.munzenberger.feed.engine.OnceFeedPoller;
import com.munzenberger.feed.log.ConsoleAppender;
import com.munzenberger.feed.log.DefaultFormatter;
import com.munzenberger.feed.log.DefaultLogger;
import com.munzenberger.feed.log.FileAppender;
import com.munzenberger.feed.log.Formatter;
import com.munzenberger.feed.log.Logger;

public class App {

	private static final Logger logger = new DefaultLogger();

	private static String feeds = "feeds.xml";
	private static String processed = null;
	private static boolean noop = false;
	private static boolean once = false;
	private static String logFile = null;
	private static boolean help = false;

	public static void main( String[] args ) throws Exception {

		Options options = buildOptions();
		parseCommandLine(args, options);

		if (help) {
			printOptions(options);
			System.exit(0);
		}

		final File file = new File(feeds);
		if (!file.canRead()) {
			System.err.println(String.format("Configuration file not found: %s", feeds));
			printOptions(options);
			System.exit(1);
		}

		Formatter formatter = new DefaultFormatter();

		logger.addAppender(new ConsoleAppender(formatter));

		if (logFile != null) {
			logger.addAppender(new FileAppender(logFile, formatter));
		}

		final FeedPoller poller;

		if (noop) {
			logger.log("Executing in NOOP mode: All items will be marked as processed without executing handlers.");
			poller = new NoopFeedPoller(file, processed, logger);
		}
		else if (once) {
			poller = new OnceFeedPoller(file, processed, logger);
		}
		else {
			poller = new FeedPoller(file, processed, logger);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.log("Shutting down...");
				poller.stop();
			}
		});

		poller.start();
	}

	private static Options buildOptions() {

		return new Options()
			.addOption("feeds", true, "path to feeds configuration file")
			.addOption("processed", true, "directory to write processed items files to")
			.addOption("log", true, "file to write log to")
			.addOption("noop", false, "mark all items as processed without executing handlers, then exit")
			.addOption("once", false, "process feeds once and then exit")
			.addOption("help", false, "print help");
	}

	private static void parseCommandLine(String[] args, Options options) throws ParseException {

		CommandLineParser cmdParser = new DefaultParser();
		CommandLine line = cmdParser.parse(options, args);

		if (line.hasOption("feeds")) {
			feeds = line.getOptionValue("feeds");
		}

		if (line.hasOption("processed")) {
			processed = line.getOptionValue("processed");
		}

		if (line.hasOption("log")) {
			logFile = line.getOptionValue("log");
		}

		noop = line.hasOption("noop");
		once = line.hasOption("once");
		help = line.hasOption("help");
	}

	private static void printOptions(Options options) {

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("feed-buddy [options]", options);
	}
}
