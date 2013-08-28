package com.munzenberger.feed.config;

public class ConfigParserException extends Exception {

	private static final long serialVersionUID = 7935092019601775863L;

	public ConfigParserException() {
	}
	
	public ConfigParserException(String message) {
		super(message);
	}
	
	public ConfigParserException(Throwable cause) {
		super(cause);
	}
	
	public ConfigParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
