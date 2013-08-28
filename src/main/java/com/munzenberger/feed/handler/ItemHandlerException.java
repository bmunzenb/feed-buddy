package com.munzenberger.feed.handler;

public class ItemHandlerException extends Exception {

	private static final long serialVersionUID = 4393475585350584933L;

	public ItemHandlerException() {
	}
	
	public ItemHandlerException(String message) {
		super(message);
	}
	
	public ItemHandlerException(Throwable cause) {
		super(cause);
	}
	
	public ItemHandlerException(String message, Throwable cause) {
		super(message, cause);
	}
}
