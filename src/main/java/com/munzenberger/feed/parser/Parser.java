package com.munzenberger.feed.parser;

import java.net.URL;

import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.parser.rss.RSSParserException;

public interface Parser {
	
	public RSS parse(URL url) throws RSSParserException;
}
