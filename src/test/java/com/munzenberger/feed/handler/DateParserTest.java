package com.munzenberger.feed.handler;

import java.util.Date;

import junit.framework.TestCase;

public class DateParserTest extends TestCase {
	
	public void testParseDate() throws Exception {
		
		Date date = DateParser.parse("Sun, 09 Jan 2011 04:00:00 EST");
		assertNotNull(date);
		
		date = DateParser.parse("Sun, 05 Dec 2010 12:30:00 +0000");
		assertNotNull(date);
		
		date = DateParser.parse("1973-01-01T12:00:27.87+00:20");
		assertNotNull(date);
		
		date = DateParser.parse("1985-04-12T23:20:50.52Z");
		assertNotNull(date);
		
		date = DateParser.parse("2011-01-07T23:29:42Z");
		assertNotNull(date);
	}
}
