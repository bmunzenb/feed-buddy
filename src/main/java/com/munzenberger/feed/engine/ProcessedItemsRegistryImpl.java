package com.munzenberger.feed.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;

import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.parser.rss.Item;

public class ProcessedItemsRegistryImpl implements ProcessedItemsRegistry {

	private final Collection<String> processed = new HashSet<String>();
	
	private final File file;
	
	public ProcessedItemsRegistryImpl(Feed config) throws ProcessedItemsRegistryException {
		file = getFile(config);
		
		try {
			if (file.canRead()) {
				load(file, processed);
			}
			else {
				if (!file.createNewFile()) {
					throw new ProcessedItemsRegistryException("Could not create new items registry store file");
				}
			}
		}
		catch (Exception e) {
			throw new ProcessedItemsRegistryException("Could not initialize processed items registry", e);
		}
	}
	
	public boolean contains(Item item) {
		return processed.contains( item.getGuid() );
	}
	
	public void add(Item item) throws ProcessedItemsRegistryException {
		String key = item.getGuid();
		processed.add( key );
		
		try {
			save(file, key);
		} catch (IOException e) {
			throw new ProcessedItemsRegistryException("Could not add item to processed registry", e);
		}
	}
	
	private static File getFile(Feed config) {
		String file = config.getUrl();
		
		file = file.replace("://", "-");
		
		file = file.replace("/", "-");
		file = file.replace("|", "-");
		file = file.replace("\\", "-");
		file = file.replace("*", "-");
		file = file.replace("\"", "-");
		file = file.replace("<", "-");
		file = file.replace(">", "-");
		file = file.replace("=", "-");
		file = file.replace("%", "-");
		
		file = file.replace("?", "_");
		file = file.replace("&", "_");
		
		file = file + ".processed";
		
		return new File(file);
	}
	
	private static void load(File file, Collection<String> list) throws IOException {
		InputStream in = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		reader.close();
	}
	
	private static void save(File file, String key) throws IOException {
		PrintWriter out = new PrintWriter( new FileOutputStream(file, true), true );
		out.println(key);
		out.close();
	}
}
