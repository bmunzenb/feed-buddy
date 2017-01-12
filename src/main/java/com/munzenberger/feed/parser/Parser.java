package com.munzenberger.feed.parser;

import java.io.Reader;

import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.parser.rss.RSSParserException;

public interface Parser {
	
	public RSS parse(Reader in) throws RSSParserException;
}
