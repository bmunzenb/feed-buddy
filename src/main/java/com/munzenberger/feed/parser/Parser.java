package com.munzenberger.feed.parser;

import java.io.InputStream;

import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.parser.rss.RSSParserException;

public interface Parser {

	public RSS parse(InputStream in) throws RSSParserException;
}
