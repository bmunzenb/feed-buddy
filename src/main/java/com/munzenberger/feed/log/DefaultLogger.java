package com.munzenberger.feed.log;

import java.util.LinkedList;
import java.util.List;

public class DefaultLogger implements Logger {

	private final List<Appender> appenders = new LinkedList<>();

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
		for (Appender a : appenders) {
			a.append(tag, message, t);
		}
	}
}
