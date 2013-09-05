package com.munzenberger.feed.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileAndConsoleLogger implements Logger {

	private final File file;
	
	public FileAndConsoleLogger(String feeds) {
		file = new File(feeds + ".log");
	}
	
	public void debug(String message) {
		log(message);
	}
	
	public void info(String message) {
		log(message);
	}
	
	public void error(String message, Throwable t) {
		log(message);
		log(t);
	}
	
	private static String time() {
		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a - ").format(new Date());
	}
	
	private void log(String message) {
		message = time() + message;
		System.out.println(message);
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(file, true), true);
			out.println(message);
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void log(Throwable cause) {
		cause.printStackTrace(System.err);
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(file, true), true);
			cause.printStackTrace(out);
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
