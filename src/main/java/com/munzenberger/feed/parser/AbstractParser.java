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
