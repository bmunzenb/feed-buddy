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
