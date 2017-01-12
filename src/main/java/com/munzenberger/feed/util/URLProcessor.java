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

		URLConnection conn = url.openConnection();
		return getResponse(conn);
	}

	private static URLResponse getResponse(URLConnection conn) throws IOException {

		// handle redirects
		if (conn instanceof HttpURLConnection) {
			int response = ((HttpURLConnection)conn).getResponseCode();
			if (redirectCodes.contains(response)) {
				String location = conn.getHeaderField("Location");
				if (location != null) {
					return getResponse(new URL(location));
				}
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
