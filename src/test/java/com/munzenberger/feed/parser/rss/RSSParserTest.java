package com.munzenberger.feed.parser.rss;

import java.net.URL;

import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.parser.rss.RSS;
import com.munzenberger.feed.parser.rss.RSSParser;

import junit.framework.TestCase;

public class RSSParserTest extends TestCase {
	
	public void testParseNamespace() throws Exception {
		
		URL rssXml = RSSParserTest.class.getResource("rss.xml");
		
		RSS rss = RSSParser.getInstance().parse(rssXml);
		
		for (Channel c : rss.getChannels()) {
			for (Item i : c.getItems()) {
				System.out.println(i.getDescription());
				return;
			}
		}
	}

}
