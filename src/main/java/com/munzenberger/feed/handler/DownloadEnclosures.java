/*
 * Copyright 2020 Brian Munzenberger
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

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Enclosure;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.util.DataTransfer;
import com.munzenberger.feed.util.DateParser;
import com.munzenberger.feed.util.Formatter;
import com.munzenberger.feed.util.URLProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;

public class DownloadEnclosures implements ItemHandler {

	private String targetDir = ".";

	private String filter = null;

	@SuppressWarnings("unused")
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public void process(Item item, Logger logger) throws ItemHandlerException {
		for (Enclosure e : item.getEnclosures()) {
			process(e, item.getPubDate(), logger);
		}
	}

	protected void process(Enclosure e, String pubDate, Logger logger) throws ItemHandlerException {

		if (evaluate(filter, e)) {

			File file = process(e.getUrl(), logger);

			Date date = DateParser.parse(pubDate, logger);
			if (date != null) {
				if (!file.setLastModified(date.getTime())) {
					logger.log("Could not set last modified time on file: " + file);
				}
			}
		}
	}

	protected File process(String downloadURL, Logger logger) throws ItemHandlerException {

		URL url;

		try {
			url = new URL(downloadURL);
		} catch (MalformedURLException ex) {
			throw new ItemHandlerException(ex);
		}

		File file = getLocalFile(url);

		try {
			download(url, file, logger);
		}
		catch (IOException ex) {
			if (!file.delete()) {
				logger.log("Could not delete file: " + file);
			}
			throw new ItemHandlerException(ex);
		}

		return file;
	}

	public File getLocalFile(URL url) throws ItemHandlerException {

		String file = url.getPath();

		int i = file.lastIndexOf('/');
		if (i >= 0) {
			file = file.substring(i+1);
		}

		try {
			file = URLDecoder.decode(file, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ItemHandlerException(e);
		}

		String ext = "";
		i = file.lastIndexOf('.');
		if (i > 0) {
			ext = file.substring(i);
			file = file.substring(0, i);
		}

		file = Formatter.fileName(file);
		ext = Formatter.fileName(ext);

		String localPath = targetDir + File.separator + file + ext;
		File localFile = new File(localPath);

		if (localFile.exists()) {
			// append a timestamp to make this file unique
			localPath = targetDir + File.separator + file + "-" + System.currentTimeMillis() + ext;
			localFile = new File(localPath);

			if (localFile.exists()) {
				throw new ItemHandlerException("File already exists: " + file);
			}
		}

		return localFile;
	}

	protected void download(URL url, File file, Logger logger) throws IOException {
		long time = System.currentTimeMillis();		
		long bytes;

		logger.log("Transferring " + url + " -> " + file);

		InputStream in = URLProcessor.getResponse(url).getInputStream();
		OutputStream out = new FileOutputStream(file);

		bytes = DataTransfer.transfer(in, out);

		in.close();
		out.flush();
		out.close();

		time = System.currentTimeMillis() - time;
		logger.log(Formatter.fileSize(bytes) + " transferred in " + Formatter.elapsedTime(time));
	}

	protected boolean evaluate(String filter, Enclosure enclosure) {

		boolean eval = true;

		if (filter != null) {
			eval = enclosure.getUrl().matches(filter);
		}

		return eval;
	}
}
