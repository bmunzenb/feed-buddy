package com.munzenberger.feed.parser;

public class ParserException extends Exception {

	private static final long serialVersionUID = -7093521940687783117L;

	public ParserException() {
	}
	
	public ParserException(String message) {
		super(message);
	}
	
	public ParserException(Throwable cause) {
		super(cause);
	}
	
	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
