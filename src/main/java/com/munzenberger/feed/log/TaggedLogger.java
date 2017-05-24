package com.munzenberger.feed.log;

public class TaggedLogger implements Logger {

	private final String tag;
	private final Logger logger;

	public TaggedLogger(String tag, Logger logger) {
		this.tag = tag;
		this.logger = logger;
	}

	@Override
	public void addAppender(Appender appender) {
		logger.addAppender(appender);
	}

	@Override
	public void log(String message) {
		logger.log(tag, message);
	}

	@Override
	public void log(String tag, String message) {
		logger.log(tag, message);
	}

	@Override
	public void log(String message, Throwable t) {
		logger.log(tag, message, t);
	}

	@Override
	public void log(String tag, String message, Throwable t) {
		logger.log(tag, message, t);
	}
}
