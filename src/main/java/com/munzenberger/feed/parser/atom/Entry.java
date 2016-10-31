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
