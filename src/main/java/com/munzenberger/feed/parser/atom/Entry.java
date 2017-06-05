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
package com.munzenberger.feed.parser.atom;

import java.util.LinkedList;
import java.util.List;

public class Entry {

	private String title;

	private String published;

	private String id;

	private Author author;

	private Content content;

	private final List<Link> links = new LinkedList<>();
	private final List<String> categories = new LinkedList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void addLink(Link l) {
		links.add(l);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void addCategory(String category) {
		this.categories.add(category);
	}

	public List<String> getCategories() {
		return categories;
	}
}
