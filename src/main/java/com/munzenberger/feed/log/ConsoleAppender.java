package com.munzenberger.feed.log;

public class ConsoleAppender implements Appender {

	private final Formatter formatter;

	public ConsoleAppender(Formatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public void append(String tag, String message, Throwable t) {
		System.out.println(formatter.format(tag, message, t));
	}
}
