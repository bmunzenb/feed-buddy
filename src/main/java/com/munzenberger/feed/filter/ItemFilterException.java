package com.munzenberger.feed.filter;

public class ItemFilterException extends Exception {

	private static final long serialVersionUID = -6339642265582745463L;

	public ItemFilterException() {
		super();
	}

	public ItemFilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ItemFilterException(String message) {
		super(message);
	}

	public ItemFilterException(Throwable cause) {
		super(cause);
	}
}
