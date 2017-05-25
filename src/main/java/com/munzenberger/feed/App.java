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
import com.munzenberger.feed.log.ConsoleAppender;
import com.munzenberger.feed.log.DefaultLogger;
import com.munzenberger.feed.log.FileAppender;
import com.munzenberger.feed.log.Logger;

public class App {

	private static final Logger logger = new DefaultLogger();

	private static String feeds = "feeds.xml";
	private static boolean noop = false;
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

		logger.addAppender(new ConsoleAppender());

		if (logFile != null) {
			logger.addAppender(new FileAppender(logFile));
		}

		final FeedPoller poller;

		if (noop) {
			logger.log("Executing in NOOP mode: All items will be marked as processed without executing handlers.");
			poller = new NoopFeedPoller(file, logger);
		}
		else {
			poller = new FeedPoller(file, logger);
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

		Options options = new Options();
		options.addOption("feeds", true, "the feeds configuration file");
		options.addOption("log", true, "file to write log to");
		options.addOption("noop", false, "mark all items as processed without executing handlers, then exit");
		options.addOption("help", false, "print help");

		return options;
	}

	private static void parseCommandLine(String[] args, Options options) throws ParseException {

		CommandLineParser cmdParser = new DefaultParser();
		CommandLine line = cmdParser.parse(options, args);

		if (line.hasOption("feeds")) {
			feeds = line.getOptionValue("feeds");
		}

		if (line.hasOption("log")) {
			logFile = line.getOptionValue("log");
		}

		noop = line.hasOption("noop");
		help = line.hasOption("help");
	}

	private static void printOptions(Options options) {

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("feed-buddy [options]", options);
	}
}
