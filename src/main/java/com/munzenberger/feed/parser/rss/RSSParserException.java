package com.munzenberger.feed.parser.rss;

public class RSSParserException extends Exception {

	private static final long serialVersionUID = -7093521940687783117L;

	public RSSParserException() {
	}
	
	public RSSParserException(String message) {
		super(message);
	}
	
	public RSSParserException(Throwable cause) {
		super(cause);
	}
	
	public RSSParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
