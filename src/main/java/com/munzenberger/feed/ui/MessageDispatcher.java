package com.munzenberger.feed.ui;

public interface MessageDispatcher {

	public void info(String message);
	
	public void debug(String message);
	
	public void error(String message, Throwable cause);
}
