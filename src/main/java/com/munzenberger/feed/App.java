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
import com.munzenberger.feed.ui.MessageDispatcher;
import com.munzenberger.feed.ui.MessageDispatcherImpl;

public class App {
	
	private static String feeds = "feeds.xml";
	private static boolean noop = false;
	
	public static void main( String[] args ) throws Exception {
		
		System.setProperty("http.agent", "feed-buddy");
				
		parseCommandLine(args);
		
		final MessageDispatcher dispatcher = new MessageDispatcherImpl(feeds);
		final File file = new File(feeds);
		final FeedPoller poller;
		
		if (noop) {
			dispatcher.info("Executing in NOOP mode.");
			poller = new NoopFeedPoller(file, dispatcher);
		}
		else {
			poller = new FeedPoller(file, dispatcher);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				dispatcher.info("Shutting down...");
				poller.stop();
			}
		});
		
		poller.start();
		
		while (!noop) {
			Thread.sleep(5000);
		}
	}
	
	private static void parseCommandLine(String[] args) throws ParseException {
		
		@SuppressWarnings("static-access")
		Option feedsOption = OptionBuilder.withArgName("file").hasArg().withDescription("the feeds configuration file").create("feeds");
		
		Option noopOption = new Option("noop", "marks all feeds as processed");
		
		Options options = new Options();
		options.addOption(feedsOption);
		options.addOption(noopOption);
		
		CommandLineParser cmdParser = new BasicParser();
		CommandLine line = cmdParser.parse(options, args);
		
		if (line.hasOption("feeds")) {
			feeds = line.getOptionValue("feeds");
		}
		
		noop = line.hasOption("noop");
	}
}
