package com.munzenberger.feed.util;

import java.io.InputStream;

public class URLResponse {

	private final String contentType;
	private final InputStream in;
	
	public URLResponse(String contentType, InputStream in) {
		this.contentType = contentType;
		this.in = in;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public InputStream getInputStream() {
		return in;
	}
}
