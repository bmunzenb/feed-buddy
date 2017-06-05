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
package com.munzenberger.feed.parser;

import java.io.Reader;
import java.net.URL;

import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.util.URLProcessor;
import com.munzenberger.feed.util.URLResponse;
import com.munzenberger.feed.util.URLResponseDecoder;

public abstract class AbstractParser implements Parser {

	@Override
	public RSS parse(URL url) throws ParserException {
		
		try {
			
			URLResponse response = URLProcessor.getResponse(url);
			Reader reader = URLResponseDecoder.decodeForXML(response);
			return parse(reader);
		}
		catch (Exception e) {
			
			throw new ParserException("Failed to parse RSS", e);
		}
	}
	
	protected abstract RSS parse(Reader in) throws Exception;
}
