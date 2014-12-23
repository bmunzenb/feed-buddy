package com.munzenberger.feed.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Logger {
	
	private static final String separator = System.getProperty("line.separator");
	
	private final List<Appender> appenders = new LinkedList<Appender>();
	
	public void addAppender(Appender appender) {
		appenders.add(appender);
	}

	public void info(String message) {
		append("[INFO]", message);
	}

	public void error(String message, Throwable cause) {
		append("[ERROR]", message, cause);
	}
	
	private void append(String level, String message) {
		append(level, message, null);
	}
	
	private void append(String level, String message, Throwable cause) {
		String formattedMessage = formatMessage(level, message, cause);
		for (Appender a : appenders) {
			a.append(formattedMessage);
		}
	}
	
	private static String time() {
		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new Date());
	}
	
	private static String formatMessage(String level, String message, Throwable cause) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(time()).append(" ").append(level).append(" ").append(message);
		
		if (cause != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			cause.printStackTrace(pw);
			pw.flush();
			pw.close();
			sb.append(separator).append(sw.toString());
		}
		
		return sb.toString();
	}
}
