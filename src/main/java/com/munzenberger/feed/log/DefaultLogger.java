package com.munzenberger.feed.log;

import java.util.LinkedList;
import java.util.List;

public class DefaultLogger implements Logger {

	private final Formatter formatter;
	private final List<Appender> appenders = new LinkedList<>();

	public DefaultLogger() {
		this(new DefaultFormatter());
	}

	public DefaultLogger(Formatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public void addAppender(Appender appender) {
		appenders.add(appender);
	}

	@Override
	public void log(String message) {
		log(null, message);
	}

	@Override
	public void log(String tag, String message) {
		log(tag, message, null);
	}

	@Override
	public void log(String message, Throwable t) {
		log(null, message, t);
	}

	@Override
	public void log(String tag, String message, Throwable t) {
		String formattedMessage = formatter.format(tag, message, t);
		for (Appender a : appenders) {
			a.append(formattedMessage);
		}
	}
}
