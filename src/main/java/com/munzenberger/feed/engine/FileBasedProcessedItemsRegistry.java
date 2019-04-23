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
import com.munzenberger.feed.util.Formatter;

public class FileBasedProcessedItemsRegistry implements ProcessedItemsRegistry {

	private final Collection<String> processed = new HashSet<String>();

	private final File file;

	public FileBasedProcessedItemsRegistry(Feed config) throws ProcessedItemsRegistryException {
		this(null, config);
	}

	public FileBasedProcessedItemsRegistry(String rootDir, Feed config) throws ProcessedItemsRegistryException {
		file = getFile(rootDir, config);

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
			throw new ProcessedItemsRegistryException("Could not initialize processed items registry: " + file, e);
		}
	}

	public boolean contains(Item item) {
		return processed.contains( item.getUniqueId() );
	}

	public void add(Item item) throws ProcessedItemsRegistryException {
		String key = item.getUniqueId();
		processed.add( key );

		try {
			save(file, key);
		} catch (IOException e) {
			throw new ProcessedItemsRegistryException("Could not add item to processed registry", e);
		}
	}

	private static File getFile(String rootDir, Feed config) {

		String file = "";

		if (rootDir != null) {
			file = rootDir + File.separator;
		}

		file += Formatter.fileName(config.getUrl());
		file += ".processed";

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
