package com.munzenberger.feed.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogger implements Logger {

	public void debug(String message) {
		log(buildMessage(message));
	}
	
	public void info(String message) {
		log(buildMessage(message));
	}
	
	public void error(String message, Throwable t) {
		log(buildMessage(message));
		log(buildMessage(t));
	}
	
	private static String buildMessage(String message) {
		return time() + message;
	}
	
	private static String buildMessage(Throwable t) {
		StringWriter s = new StringWriter();
		PrintWriter out = new PrintWriter(s);
		t.printStackTrace(out);
		out.close();
		return s.toString();
	}
	
	private static String time() {
		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a - ").format(new Date());
	}
	
	protected void log(String message) {
		System.out.println(message);
	}
}
