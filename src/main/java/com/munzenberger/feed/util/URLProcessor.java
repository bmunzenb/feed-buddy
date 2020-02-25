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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public final class URLProcessor {

	private static final Set<Integer> redirectCodes = new HashSet<>();

	static {
		redirectCodes.add(HttpURLConnection.HTTP_MOVED_PERM);
		redirectCodes.add(HttpURLConnection.HTTP_MOVED_TEMP);
		redirectCodes.add(HttpURLConnection.HTTP_SEE_OTHER);
		redirectCodes.add(307);  // Temporary Redirect (since HTTP/1.1)
		redirectCodes.add(308);  // Permanent Redirect (RFC 7538)
	}

	private URLProcessor() {}

	public static URLResponse getResponse(URL url) throws IOException {
		return getResponse(url, null);
	}

	public static URLResponse getResponse(URL url, String userAgent) throws IOException {
		URLConnection conn = url.openConnection();
		if (userAgent != null) {
			conn.setRequestProperty("User-agent", userAgent);
		}
		return getResponse(conn, userAgent);
	}

	private static URLResponse getResponse(URLConnection conn, String userAgent) throws IOException {

		// handle redirects
		if (conn instanceof HttpURLConnection) {
			int response = ((HttpURLConnection)conn).getResponseCode();
			if (redirectCodes.contains(response)) {
				String location = conn.getHeaderField("Location");
				if (location == null) {
					throw new IOException(String.format("Redirect response (%d) with no 'location' in header: %s", response, String.valueOf(conn.getHeaderFields())));
				}
				return getResponse(new URL(location), userAgent);
			}
		}

		String contentEncoding = conn.getHeaderField("Content-Encoding");
		String contentType = conn.getHeaderField("Content-Type");

		InputStream in = conn.getInputStream();

		// handle gzip encoded responses
		if ("gzip".equalsIgnoreCase(contentEncoding)) {
			in = new GZIPInputStream(in);
		}

		return new URLResponse(contentType, in);
	}
}
