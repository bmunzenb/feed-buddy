package com.munzenberger.feed.handler;

import java.io.File;

import junit.framework.TestCase;

public class DownloadEnclosuresTest extends TestCase {

	public void testGetLocalFile() throws Exception {
		
		DownloadEnclosures handler = new DownloadEnclosures();
		
		String url = "http://www.test.com/download.mp3?id=1";
		
		File f1 = handler.getLocalFile(url);
		
		assertNotNull(f1);
		f1.deleteOnExit();
		
		final String separator = System.getProperty("file.separator");
		
		assertEquals("." + separator + "download.mp3", f1.getPath());
		
		url = "http://www.test.com/download.mp3?id=2";
		
		File f2 = handler.getLocalFile(url);
		
		assertNotNull(f2);
		f2.deleteOnExit();
		
		int hashcode = url.hashCode();
		
		assertEquals("." + separator + "download-" + hashcode + ".mp3", f2.getPath());
	}

	public void testGetLocalFileWithEscapes() throws Exception {

		DownloadEnclosures handler = new DownloadEnclosures();

		String url = "http://www.test.com/403+-+Raw+-+5%3A1%3A15%2C+3.15+PM.mp3";

		File f1 = handler.getLocalFile(url);

		assertNotNull(f1);
		f1.deleteOnExit();

		final String separator = System.getProperty("file.separator");

		assertEquals("." + separator + "403 - Raw - 5-1-15, 3.15 PM.mp3", f1.getPath());
	}
}
