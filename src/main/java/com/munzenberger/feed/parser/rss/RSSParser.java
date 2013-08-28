package com.munzenberger.feed.parser.rss;

import java.net.URL;

import org.apache.commons.digester.Digester;

import com.munzenberger.feed.parser.Parser;

public class RSSParser implements Parser {
	
	private static final RSSParser instance = new RSSParser();
	
	private RSSParser() {
	}
	
	public static RSSParser getInstance() {
		return instance;
	}
	
	protected static Digester getDigester() {
		
		Digester digester = new Digester();
		
		digester.setValidating(false);
		
		digester.addObjectCreate("rss", RSS.class);
		digester.addSetProperties("rss");
		
		digester.addObjectCreate("rss/channel", Channel.class);
		digester.addSetProperties("rss/channel");
		digester.addCallMethod("rss/channel/title", "setTitle", 0);
		digester.addSetNext("rss/channel", "addChannel");
		
		digester.addObjectCreate("rss/channel/item", Item.class);
		digester.addSetProperties("rss/channel/item");
		digester.addCallMethod("rss/channel/item/title", "setTitle", 0);
		digester.addCallMethod("rss/channel/item/guid", "setGuid", 0);
		digester.addCallMethod("rss/channel/item/link", "setLink", 0);
		digester.addCallMethod("rss/channel/item/description", "setDescription", 0);
		digester.addCallMethod("rss/channel/item/author", "setAuthor", 0);
		digester.addCallMethod("rss/channel/item/pubDate", "setPubDate", 0);
		digester.addSetNext("rss/channel/item", "addItem");
		
		digester.addObjectCreate("rss/channel/item/enclosure", Enclosure.class);
		digester.addSetProperties("rss/channel/item/enclosure");
		digester.addSetNext("rss/channel/item/enclosure", "addEnclosure");
		
		digester.setNamespaceAware(true);
		
		digester.setRuleNamespaceURI("http://purl.org/rss/1.0/modules/content/");
		// some feeds use <content:encoded> for their HTML
		digester.addCallMethod("rss/channel/item/encoded", "setDescription", 0);
		
		return digester;
	}
	
	public RSS parse(URL url) throws RSSParserException {
		try {
			return (RSS) getDigester().parse(url);
		}
		catch (Exception e) {
			throw new RSSParserException("Failed to parse RSS at " + url, e);
		}
	}
}
