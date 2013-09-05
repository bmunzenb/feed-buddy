package com.munzenberger.feed.util;

import junit.framework.TestCase;

public class FormatterTest extends TestCase {

	private static final long ONE_SECOND = 1000;
	private static final long ONE_MINUTE = 60 * ONE_SECOND;
	private static final long ONE_HOUR = 60 * ONE_MINUTE;
	
	public void testElapsedTime() {
		
		assertEquals("1 ms", Formatter.elapsedTime(1));
		assertEquals("1 sec 0 ms", Formatter.elapsedTime(ONE_SECOND));
		assertEquals("1 min 0 sec", Formatter.elapsedTime(ONE_MINUTE));
		assertEquals("1 hr 0 min 0 sec", Formatter.elapsedTime(ONE_HOUR));
		
		assertEquals("1 sec 1 ms", Formatter.elapsedTime(ONE_SECOND + 1));
		assertEquals("1 min 1 sec", Formatter.elapsedTime(ONE_MINUTE + ONE_SECOND));
		assertEquals("1 min 1 sec", Formatter.elapsedTime(ONE_MINUTE + ONE_SECOND + 1));
		assertEquals("1 hr 1 min 1 sec", Formatter.elapsedTime(ONE_HOUR + ONE_MINUTE + ONE_SECOND));
		assertEquals("1 hr 1 min 1 sec", Formatter.elapsedTime(ONE_HOUR + ONE_MINUTE + ONE_SECOND + 1));
	}
}
