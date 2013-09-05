package com.munzenberger.feed.log;

import com.munzenberger.feed.log.Logger;

public class ConsoleLogger implements Logger {

	public void debug(String message) {
		println("DEBUG: " + message);
	}

	public void error(String message, Throwable cause) {
		System.err.println("ERROR: " + message);
	}

	public void info(String message) {
		println("INFO: " + message);
	}

	private void println(String message) {
		System.out.println(message);
	}
}
