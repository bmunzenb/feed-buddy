package com.munzenberger.feed.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileAppender implements Appender {

	private final File file;
	
	public FileAppender(String path) {
		this(new File(path));
	}
	
	public FileAppender(File file) {
		this.file = file;
	}
	
	@Override
	public void append(String message) {
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(file, true), true);
			out.println(message);
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
