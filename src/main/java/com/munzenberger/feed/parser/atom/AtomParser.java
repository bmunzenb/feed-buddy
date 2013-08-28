package com.munzenberger.feed.parser.atom;

import java.net.URL;

import org.apache.commons.digester.Digester;

import com.munzenberger.feed.parser.Parser;
import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.parser.rss.RSS;

public class AtomParser implements Parser {

	private static final AtomParser instance = new AtomParser();
	
	private AtomParser() {
	}
	
	public static AtomParser getInstance() {
		return instance;
	}
	
	protected static Digester getDigester() {
		
		Digester digester = new Digester();
		
		digester.setValidating(false);
		
		digester.addObjectCreate("feed", Atom.class);
		digester.addSetProperties("feed");
		digester.addCallMethod("feed/title", "setTitle", 0);
		
		digester.addObjectCreate("feed/entry", Entry.class);
		digester.addSetProperties("feed/entry");
		digester.addCallMethod("feed/entry/title", "setTitle", 0);
		digester.addCallMethod("feed/entry/id", "setId", 0);
		digester.addCallMethod("feed/entry/published", "setPublished", 0);
		digester.addSetNext("feed/entry", "addEntry");
		
		digester.addObjectCreate("feed/entry/author", Author.class);
		digester.addSetProperties("feed/entry/author");
		digester.addCallMethod("feed/entry/author/name", "setName", 0);
		digester.addCallMethod("feed/entry/author/email", "setEmail", 0);
		digester.addSetNext("feed/entry/author", "setAuthor");
		
		digester.addObjectCreate("feed/entry/link", Link.class);
		digester.addSetProperties("feed/entry/link");
		digester.addSetNext("feed/entry/link", "addLink");
		
		digester.addObjectCreate("feed/entry/content", Content.class);
		digester.addSetProperties("feed/entry/content");
		digester.addCallMethod("feed/entry/content", "setValue", 0);
		digester.addSetNext("feed/entry/content", "setContent");
		
		return digester;
	}
	
	protected static RSS toRSS(Atom atom) {
		RSS rss = new RSS();
		
		Channel channel = new Channel();
		channel.setTitle(atom.getTitle());
		
		for (Entry e : atom.getEntries()) {
			Item item = new Item();
			
			item.setTitle(e.getTitle());
			item.setPubDate(e.getPublished());
			item.setGuid(e.getId());
			
			if (e.getContent() != null) {
				item.setDescription(e.getContent().getValue());
			}
			
			if (e.getAuthor() != null && e.getAuthor().getEmail() != null) {
				item.setAuthor(e.getAuthor().getEmail());
			}
			
			for (Link l : e.getLinks()) {
				if (l.getRel() == null || "alternate".equalsIgnoreCase(l.getRel())) {
					item.setLink(l.getHref());
				}
				
				// eventually handle enclosures ...
			}
			
			channel.addItem(item);
		}

		rss.addChannel(channel);
		
		return rss;
	}
	
	public RSS parse(URL url) throws AtomParserException {
		try {
			Atom atom = (Atom) getDigester().parse(url);
			return toRSS(atom);
		}
		catch (Exception e) {
			throw new AtomParserException("Failed to parse Atom at " + url, e);
		}
	}
}
