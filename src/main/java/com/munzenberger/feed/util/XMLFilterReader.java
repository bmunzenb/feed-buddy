package com.munzenberger.feed.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class XMLFilterReader extends FilterReader {
	
	public XMLFilterReader(Reader in) {
		super(in);
	}

	@Override
	public int read() throws IOException {
		return super.read();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int read = super.read(cbuf, off, len);
		
		for (int i = off; i < off+len; i++) {
			if (!isValidXMLChar(cbuf[i])) {
				cbuf[i] = ' ';
			}
		}
		
		return read;
	}
	
	private static boolean isValidXMLChar(char c) {

		// from https://www.w3.org/TR/xml/#charsets
		return c == 0x9 || c == 0xA || c == 0xD ||
				0x20 <= c && c <= 0xD7FF ||
				0xE000 <= c && c <= 0xFFFD ||
				0x10000 <= c && c <= 0x10FFFF;
	}
}
