package com.munzenberger.feed.handler;

public class ItemHandlerFactoryException extends Exception {

	private static final long serialVersionUID = 6392039107909122715L;

	public ItemHandlerFactoryException() {
	}
	
	public ItemHandlerFactoryException(String message) {
		super(message);
	}
	
	public ItemHandlerFactoryException(Throwable cause) {
		super(cause);
	}
	
	public ItemHandlerFactoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
