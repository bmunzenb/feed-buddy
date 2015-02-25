package com.munzenberger.feed.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public final class URLProcessor {

	private URLProcessor() {}

	public static InputStream getInputStream(URL url) throws IOException {
		
		URLConnection conn = url.openConnection();
		
		if (conn instanceof HttpURLConnection) {
			return getHttpInputStream((HttpURLConnection)conn);
		}
		else {
			return getInputStream(conn);
		}
	}
	
	private static InputStream getHttpInputStream(HttpURLConnection conn) throws IOException {
		
		int responseCode = conn.getResponseCode();
		
		// follow redirects
		if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
			String location = conn.getHeaderField("Location");
			if (location != null) {
				URL redirect = new URL(location);
				return getInputStream(redirect);
			}
		}
		
		return getInputStream(conn);
	}
	
	private static InputStream getInputStream(URLConnection conn) throws IOException {
		
		String contentEncoding = conn.getHeaderField("Content-Encoding");

		InputStream in = conn.getInputStream();
		
		// handle gzip encoded responses
		if ("gzip".equalsIgnoreCase(contentEncoding)) {
			in = new GZIPInputStream(in);
		}
		
		return in;
	}
}