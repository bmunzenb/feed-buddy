package com.munzenberger.feed.parser;

import java.net.URL;

import com.munzenberger.feed.parser.rss.RSS;

public interface Parser {
	
	public RSS parse(URL url) throws ParserException;
}
