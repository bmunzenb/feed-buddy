package com.munzenberger.feed.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultFormatter implements Formatter {

	private static final String separator = System.getProperty("line.separator");

	@Override
	public String format(String tag, String message, Throwable t) {

		StringBuilder sb = new StringBuilder();

		sb.append(timestamp());

		if (tag != null) {
			sb.append(" [").append(tag).append("]");
		}

		sb.append(" ").append(message);

		if (t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.flush();
			pw.close();
			sb.append(separator).append(sw.toString());
		}

		return sb.toString();
	}

	protected String timestamp() {
		return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a").format(new Date());
	}

	public static void main(String[] args) {
		DefaultFormatter formatter = new DefaultFormatter();
		System.out.println(formatter.format("tag", "message", new Throwable()));
	}
}
