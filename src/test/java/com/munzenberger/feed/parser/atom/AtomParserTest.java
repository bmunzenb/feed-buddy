package com.munzenberger.feed.parser.atom;

import java.net.URL;

import com.munzenberger.feed.parser.atom.AtomParser;
import com.munzenberger.feed.parser.rss.RSS;

import junit.framework.TestCase;

public class AtomParserTest extends TestCase {

	public void testParse() throws Exception {
		URL atomXml = AtomParserTest.class.getResource("atom.xml");
		RSS rss = AtomParser.getInstance().parse(atomXml.openStream());
		assertNotNull(rss);
	}
}
