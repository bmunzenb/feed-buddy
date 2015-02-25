package com.munzenberger.feed.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Enclosure;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.util.DataTransfer;
import com.munzenberger.feed.util.DateParser;
import com.munzenberger.feed.util.Formatter;
import com.munzenberger.feed.util.URLProcessor;

public class DownloadEnclosures implements ItemHandler {

	private String targetDir = ".";
	
	private String filter = null;

	private boolean overwriteExisting = false;
	
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
	
	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setOverwriteExisting(String overwrite) {
		this.overwriteExisting = Boolean.valueOf(overwrite);
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
				file.setLastModified(date.getTime());
			}
		}
	}

	protected File process(String downloadURL, Logger logger) throws ItemHandlerException {
		File file = getLocalFile( downloadURL );
		
		URL url;
		
		try {
			url = new URL(downloadURL);
		} catch (MalformedURLException ex) {
			throw new ItemHandlerException(ex);
		}
		
		try {
			download(url, file, logger);
		}
		catch (IOException ex) {
			if (!file.delete()) {
				logger.info("Could not delete file: " + file);
			}
			throw new ItemHandlerException(ex);
		}
		
		return file;
	}
	
	public File getLocalFile(String url) throws ItemHandlerException {
		String filePath = url;
		filePath = filePath.substring( filePath.lastIndexOf("/") + 1 );
		
		int urlHash = url.hashCode();
		
		int i = filePath.indexOf('?');
		if (i > 0) {
			filePath = filePath.substring(0, i);
		}
		
		filePath = targetDir + System.getProperty("file.separator") + filePath;
	
		File file = new File(filePath);
		try {
			
			boolean newFileCreated = file.createNewFile();
			
			if (!newFileCreated) {
				int j = filePath.lastIndexOf('.');
				filePath = filePath.substring(0, j) + "-" + String.valueOf(urlHash) + filePath.substring(j);
				file = new File(filePath);
				newFileCreated = file.createNewFile();
			}
			
			if (!newFileCreated && !this.overwriteExisting) {
				throw new ItemHandlerException("File " + file + " already exists, skipping download of " + url);
			}
		}
		catch (IOException ex) {
			throw new ItemHandlerException("Could not create file", ex);
		}
		
		return file;
	}
	
	protected void download(URL url, File file, Logger logger) throws IOException {
		long time = System.currentTimeMillis();		
		long bytes = 0;
		
		InputStream in = URLProcessor.getInputStream(url);
		OutputStream out = new FileOutputStream(file);
		
		bytes = DataTransfer.transfer(in, out);
		
		in.close();
		out.flush();
		out.close();
		
		time = System.currentTimeMillis() - time;
		logger.info(url + " -> " + file + " (" + Formatter.fileSize(bytes) + " in " + Formatter.elapsedTime(time) + ")");
	}

	protected boolean evaluate(String filter, Enclosure enclosure) {

		boolean eval = true;

		if (filter != null) {
			eval = enclosure.getUrl().matches(filter);
		}

		return eval;
	}
}
