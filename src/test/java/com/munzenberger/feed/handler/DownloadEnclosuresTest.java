/*
 * Copyright 2017 Brian Munzenberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.munzenberger.feed.handler;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

public class DownloadEnclosuresTest extends TestCase {

	public void testGetLocalFile() throws Exception {
		
		DownloadEnclosures handler = new DownloadEnclosures();
		
		URL url = new URL("http://www.test.com/download.mp3?id=1");
		
		File f1 = handler.getLocalFile(url);
		
		assertNotNull(f1);
		f1.deleteOnExit();

		assertEquals("." + File.separator + "download.mp3", f1.getPath());
	}

	public void testGetLocalFileWithEscapes() throws Exception {

		DownloadEnclosures handler = new DownloadEnclosures();

		URL url = new URL("http://www.test.com/403+-+Raw+-+5%3A1%3A15%2C+3.15+PM.mp3");

		File f1 = handler.getLocalFile(url);

		assertNotNull(f1);
		f1.deleteOnExit();

		assertEquals("." + File.separator + "403 - Raw - 5-1-15, 3.15 PM.mp3", f1.getPath());
	}

	public void testGetLocalFileThatAlreadyExists() throws Exception {

		DownloadEnclosures handler = new DownloadEnclosures();

		URL url = new URL("http://www.test.com/download.mp3?id=1");

		File f1 = handler.getLocalFile(url);
		f1.deleteOnExit();
		assertTrue(f1.createNewFile());

		File f2 = handler.getLocalFile(url);
		f2.deleteOnExit();
		assertNotSame(f1.getAbsolutePath(), f2.getAbsolutePath());
	}
}
