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
		
		HttpURLConnection.setFollowRedirects(true);
		
		URLConnection conn = url.openConnection();
		
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
