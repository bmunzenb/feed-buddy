package com.munzenberger.feed.log;

public interface Logger {

	public void info(String message);
	
	public void debug(String message);
	
	public void error(String message, Throwable cause);
}
