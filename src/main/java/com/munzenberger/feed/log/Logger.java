package com.munzenberger.feed.log;

public interface Logger {

	void addAppender(Appender appender);

	void log(String message);

	void log(String tag, String message);

	void log(String message, Throwable t);

	void log(String tag, String message, Throwable t);
}
