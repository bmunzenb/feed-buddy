package com.munzenberger.feed.engine;

public class ProcessedItemsRegistryException extends Exception {

	private static final long serialVersionUID = 5217124765544330361L;

	public ProcessedItemsRegistryException() {
	}
	
	public ProcessedItemsRegistryException(String message) {
		super(message);
	}
	
	public ProcessedItemsRegistryException(Throwable cause) {
		super(cause);
	}
	
	public ProcessedItemsRegistryException(String message, Throwable cause) {
		super(message, cause);
	}
}
