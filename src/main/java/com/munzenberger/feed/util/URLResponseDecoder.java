package com.munzenberger.feed.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class URLResponseDecoder {

	private URLResponseDecoder() {}
	
	public static Reader decodeForXML(URLResponse response) throws UnsupportedEncodingException {
		
		String encoding = "UTF-8";
		
		// TODO: this is too primitive, better to parse the charset from the content-type
		if (response.getContentType() != null && response.getContentType().toUpperCase().contains("UTF-16")) {
			encoding = "UTF-16";
		}
		
		return decodeForXML(encoding, response.getInputStream());
	}
	
	public static Reader decodeForXML(String encoding, InputStream in) throws UnsupportedEncodingException {
		
		Reader reader = new InputStreamReader(in, encoding);
		return new XMLFilterReader(reader, encoding);
	}
}
