package com.munzenberger.feed.parser.atom;

import com.munzenberger.feed.parser.rss.RSSParserException;

public class AtomParserException extends RSSParserException {

	private static final long serialVersionUID = -2396669076796079023L;

	public AtomParserException() {
	}
	
	public AtomParserException(String message) {
		super(message);
	}
	
	public AtomParserException(Throwable cause) {
		super(cause);
	}
	
	public AtomParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
