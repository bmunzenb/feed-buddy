/*
 * Copyright 2017 Brian Munzenberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.munzenberger.feed.util;

import java.text.NumberFormat;

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

		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(1);

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
