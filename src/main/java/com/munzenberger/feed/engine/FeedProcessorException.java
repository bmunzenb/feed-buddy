package com.munzenberger.feed.engine;

public class FeedProcessorException extends Exception {

	private static final long serialVersionUID = 837325893125041343L;

	public FeedProcessorException() {
	}
	
	public FeedProcessorException(String message) {
		super(message);
	}
	
	public FeedProcessorException(Throwable cause) {
		super(cause);
	}
	
	public FeedProcessorException(String message, Throwable cause) {
		super(message, cause);
	}
}
