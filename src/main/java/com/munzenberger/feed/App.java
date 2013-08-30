package com.munzenberger.feed;

import java.io.File;
import java.util.Timer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.munzenberger.feed.config.ConfigParser;
import com.munzenberger.feed.config.ConfigParserException;
import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.config.Feeds;
import com.munzenberger.feed.engine.FeedProcessor;
import com.munzenberger.feed.engine.FeedProcessorException;
import com.munzenberger.feed.ui.MessageDispatcher;
import com.munzenberger.feed.ui.MessageDispatcherImpl;

public class App {
	
	private static String feeds = "feeds.xml";
	private static boolean noop = false;
	
	private static Timer timer;
	
	public static boolean isNoop() {
		return noop;
	}
	
	public static void main( String[] args ) throws Exception {
		
		System.setProperty("http.agent", "feed-buddy");
				
		parseCommandLine(args);
		
		final MessageDispatcher dispatcher = new MessageDispatcherImpl(feeds);
		
		if (noop) {
			dispatcher.info("Executing in NOOP mode.");
		}
		
		File configFile = new File(feeds);
		
		scheduleFeeds(configFile, dispatcher);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				dispatcher.info("Shutting down...");
				timer.cancel();
			}
		});
		
		while (!noop) {
			Thread.sleep(1000 * 5); // 1000 millis * 5 secs
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
	
	public static void scheduleFeeds(File file, MessageDispatcher dispatcher) throws FeedProcessorException, ConfigParserException {
		timer = new Timer();

		Feeds config = ConfigParser.parse(file);
		scheduleFeeds(config, dispatcher);

		if (!noop) {
			ConfigListener configListener = new ConfigListener(file, timer, dispatcher);
			timer.schedule(configListener, 0, 1000 * 5);  // 1000 millis * 5 secs
		}
	}
	
	private static void scheduleFeeds(Feeds config, MessageDispatcher dispatcher) throws FeedProcessorException {
		
		dispatcher.info("Scheduling " + config.getFeeds().size() + " feed" + (config.getFeeds().size() != 1 ? "s" : "") + "...");
		
		for (Feed f : config.getFeeds()) {
			FeedProcessor p = new FeedProcessor(f, dispatcher);
			
			if (noop) {
				p.run();
			}
			else {
				long period;
				
				// period is configured in minutes
				if (f.getPeriod() > 0) {
					period = f.getPeriod();
				} else {
					period = config.getPeriod();
				}
				
				period = period * 60 * 1000; // convert to millis
				
				timer.schedule(p.getTimerTask(), 0, period);
			}
		}
	}
}
