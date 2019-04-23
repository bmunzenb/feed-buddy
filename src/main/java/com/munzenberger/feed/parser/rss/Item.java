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
package com.munzenberger.feed.parser.rss;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class Item {

	private String title;
	private String link;
	private String guid;
	private String description;
	private String author;
	private String pubDate;
	private Channel channel;
	private final List<Enclosure> enclosures = new LinkedList<>();
	private final List<String> categories = new LinkedList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		// prevent RSS feeds with Atom elements from overwriting this property
		if (isEmpty(this.link)) {
			this.link = link;
		}
	}

	public String getUniqueId() {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			String id = "";

			if (title != null) {
				id += title;
			}

			if (description != null) {
				id += description;
			}

			if (pubDate != null) {
				id += pubDate;
			}

			digest.update(id.getBytes());
			byte[] bytes = digest.digest();
			return Base64.getEncoder().encodeToString(bytes);
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public List<Enclosure> getEnclosures() {
		return enclosures;
	}

	public void addEnclosure(Enclosure e) {
		enclosures.add(e);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void addCategory(String category) {
		this.categories.add(category);
	}

	public List<String> getCategories() {
		return categories;
	}

	@Override
	public String toString() {
		return title + " (" + getUniqueId() + ")";
	}

	private static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
}
