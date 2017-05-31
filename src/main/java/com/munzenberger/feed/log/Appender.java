package com.munzenberger.feed.log;

public interface Appender {

	public void append(String tag, String message, Throwable t);
}
