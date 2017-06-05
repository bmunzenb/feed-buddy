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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class XMLFilterReader extends FilterReader {
	
	private final String encoding;
	private boolean first = true;
	
	public XMLFilterReader(Reader in, String encoding) {
		super(in);
		this.encoding = encoding;
	}

	@Override
	public int read() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int read = super.read(cbuf, off, len);
		
		if (read < 0) {
			return read;
		}
		
		int ptr = off;
		int ctr = 0;
		
		for (int i = off; i < off+read; i++) {
			
			if (first) {
				first = false;
				// strip the UTF-8 byte order mark, if present
				if ("UTF-8".equals(encoding) && cbuf[i] == '\uFEFF') {
					continue;
				}
			}
			
			if (isValidXMLChar(cbuf[i])) {
				cbuf[ptr++] = cbuf[i];
				ctr++;
			}
		}
		
		return ctr;
	}
	
	private static boolean isValidXMLChar(char c) {

		// from https://www.w3.org/TR/xml/#charsets
		return c == 0x9 || c == 0xA || c == 0xD ||
				0x20 <= c && c <= 0xD7FF ||
				0xE000 <= c && c <= 0xFFFD ||
				0x10000 <= c && c <= 0x10FFFF;
	}
}
