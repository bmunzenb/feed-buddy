package com.munzenberger.feed.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileAppender implements Appender {

	private final File file;
	private final Formatter formatter;

	public FileAppender(String path, Formatter formatter) {
		this(new File(path), formatter);
	}

	public FileAppender(File file, Formatter formatter) {
		this.file = file;
		this.formatter = formatter;
	}

	@Override
	public void append(String tag, String message, Throwable t) {
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(file, true), true);
			out.println(formatter.format(tag, message, t));
			out.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
