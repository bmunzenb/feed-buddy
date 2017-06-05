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

import java.util.HashMap;
import java.util.Map;

import com.munzenberger.feed.parser.atom.AtomParser;
import com.munzenberger.feed.parser.rss.RSSParser;

public class ParserFactory {

	private ParserFactory() {
	}
	
	private static final Map<String, Parser> parsers = new HashMap<String, Parser>();
	static {
		parsers.put("rss", RSSParser.getInstance());
		parsers.put("atom", AtomParser.getInstance());
	}
	
	public static Parser getParser(String type) throws ParserFactoryException {
		Parser p = parsers.get( type.toLowerCase() );
		if (p == null) {
			throw new ParserFactoryException("No parser found for type: " + type);
		} else {
			return p;
		}
	}
}
