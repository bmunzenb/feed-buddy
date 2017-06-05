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
