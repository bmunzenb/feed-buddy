package com.munzenberger.feed.log;

public interface Logger {

	public interface Formatter {
		String format(String tag, String message, Throwable t);
	}

	void addAppender(Appender appender);

	public void log(String message);

	public void log(String tag, String message);

	public void log(String message, Throwable t);

	public void log(String tag, String message, Throwable t);
}
