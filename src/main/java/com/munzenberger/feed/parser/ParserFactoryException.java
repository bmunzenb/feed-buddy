package com.munzenberger.feed.parser;

public class ParserFactoryException extends Exception {

	private static final long serialVersionUID = 5941203908658931748L;

	public ParserFactoryException() {
	}
	
	public ParserFactoryException(String message) {
		super(message);
	}
	
	public ParserFactoryException(Throwable cause) {
		super(cause);
	}
	
	public ParserFactoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
