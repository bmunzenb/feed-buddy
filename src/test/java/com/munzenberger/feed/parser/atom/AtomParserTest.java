package com.munzenberger.feed.parser.atom;

import java.net.URL;

import junit.framework.TestCase;

import com.munzenberger.feed.parser.rss.RSS;

public class AtomParserTest extends TestCase {

	public void testParse() throws Exception {
		URL atomXml = AtomParserTest.class.getResource("atom.xml");
		RSS rss = AtomParser.getInstance().parse(atomXml.openStream());
		assertNotNull(rss);
	}
}
