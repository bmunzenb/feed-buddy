package com.munzenberger.feed.parser.atom;

import java.net.URL;

import com.munzenberger.feed.parser.atom.AtomParser;
import com.munzenberger.feed.parser.rss.RSS;

import junit.framework.TestCase;

public class AtomParserTest extends TestCase {

	public void testParse() throws Exception {
		RSS rss = AtomParser.getInstance().parse( new URL("http://blogs.suntimes.com/ebert/atom.xml") );
		assertNotNull(rss);
	}
}
