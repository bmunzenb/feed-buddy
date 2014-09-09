package com.munzenberger.feed.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileAndConsoleLogger extends ConsoleLogger {

	private final File file;
	
	public FileAndConsoleLogger(String logFile) {
		file = new File(logFile);
	}

	@Override
	protected void log(String message) {
		super.log(message);
		
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
