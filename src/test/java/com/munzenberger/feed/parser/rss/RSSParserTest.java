package com.munzenberger.feed.parser.rss;

import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.TestCase;

public class RSSParserTest extends TestCase {

	public void testParseNamespace() throws Exception {
		URL rssXml = RSSParserTest.class.getResource("rss.xml");
		RSS rss = RSSParser.getInstance().parse(new InputStreamReader(rssXml.openStream()));
		assertNotNull(rss);
	}
}
