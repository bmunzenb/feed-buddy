package com.munzenberger.feed;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.munzenberger.feed.engine.FeedPoller;
import com.munzenberger.feed.engine.NoopFeedPoller;
import com.munzenberger.feed.log.ConsoleAppender;
import com.munzenberger.feed.log.FileAppender;
import com.munzenberger.feed.log.Logger;

public class App {
	
	private static final Logger logger = new Logger();
	
	private static String feeds = "feeds.xml";
	private static boolean noop = false;
	private static String logFile = null;
	
	public static void main( String[] args ) throws Exception {
		
		parseCommandLine(args);
		
		logger.addAppender(new ConsoleAppender());
		
		if (logFile != null) {
			logger.addAppender(new FileAppender(logFile));
		}
		
		final File file = new File(feeds);
		final FeedPoller poller;
		
		if (noop) {
			logger.info("Executing in NOOP mode.");
			poller = new NoopFeedPoller(file, logger);
		}
		else {
			poller = new FeedPoller(file, logger);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.info("Shutting down...");
				poller.stop();
			}
		});
		
		poller.start();
	}
	
	@SuppressWarnings("static-access")
	private static void parseCommandLine(String[] args) throws ParseException {
		
		Option feedsOption = OptionBuilder.withArgName("file").hasArg().withDescription("the feeds configuration file").create("feeds");
		Option logOption = OptionBuilder.withArgName("file").hasArg().withDescription("file to write log to").create("log");
		
		Option noopOption = new Option("noop", "marks all feeds as processed");
		
		Options options = new Options();
		options.addOption(feedsOption);
		options.addOption(logOption);
		options.addOption(noopOption);
		
		CommandLineParser cmdParser = new BasicParser();
		CommandLine line = cmdParser.parse(options, args);
		
		if (line.hasOption("feeds")) {
			feeds = line.getOptionValue("feeds");
		}
		
		if (line.hasOption("log")) {
			logFile = line.getOptionValue("log");
		}
		
		noop = line.hasOption("noop");
	}
}
