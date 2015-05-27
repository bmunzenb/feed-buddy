package com.munzenberger.feed.util;

import java.text.DecimalFormat;

public class Formatter {

	private Formatter() {}
	
	public static String elapsedTime(long millis) {
		
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		
		if (millis >= 1000) {
			seconds = millis / 1000;
			millis = millis % 1000;
		}
		
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds = seconds % 60;
		}
		
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes = minutes % 60;
		}
		
		StringBuilder sb = new StringBuilder();
		
		if (hours > 0) {
			sb.append(hours).append(" hr ").append(minutes).append(" min ").append(seconds).append(" sec");
		}
		else if (minutes > 0) {
			sb.append(minutes).append(" min ").append(seconds).append(" sec");
		}
		else if (seconds > 0) {
			sb.append(seconds).append(" sec");
		}
		else {
			sb.append(millis).append(" ms");
		}
		
		return sb.toString();
	}
	
	public static String fileSize(long size) {
		double b = Long.valueOf(size).doubleValue();
		String d = "bytes";
		
		if (b > 1024d) {
			b = b / 1024d;
			d = "KB";
		}
		
		if (b > 1024d) {
			b = b / 1024d;
			d = "MB";
		}
		
		DecimalFormat format = new DecimalFormat("#,###.##");
		
		return format.format(b) + " " + d;
	}

	public static String fileName(String str) {
		return str.replace("://", "-")
				.replace("/", "-")
				.replace("|", "-")
				.replace("\\", "-")
				.replace("*", "-")
				.replace("\"", "-")
				.replace("<", "-")
				.replace(">", "-")
				.replace("=", "-")
				.replace("%", "-")
				.replace(":", "-")
				.replace("?", "_")
				.replace("&", "_");
	}
}
