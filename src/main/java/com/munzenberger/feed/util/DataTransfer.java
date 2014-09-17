package com.munzenberger.feed.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DataTransfer {

	public static long transfer(InputStream in, OutputStream out) throws IOException {
		
		long bytes = 0;
		
		byte[] buffer = new byte[1024 * 16];  // 16 KB buffer
		int read = in.read(buffer);
		while (read > 0) {
			bytes += read;
			out.write(buffer, 0, read);
			read = in.read(buffer);
		}
		
		return bytes;
	}
}
