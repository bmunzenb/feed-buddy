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
