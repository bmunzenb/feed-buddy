package com.munzenberger.feed.log;

public interface Formatter {

	String format(String tag, String message, Throwable t);
}
