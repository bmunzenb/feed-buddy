package com.munzenberger.feed.config;

import java.io.File;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.SetPropertiesRule;

public class ConfigParser {

	protected static Digester getDigester() {
		
		Digester digester = new Digester();
		
		digester.setValidating(false);
		
		digester.addObjectCreate("feeds", Feeds.class);
		digester.addSetProperties("feeds");
		
		digester.addObjectCreate("feeds/handler", Handler.class);
		digester.addSetProperties("feeds/handler");
		digester.addRule("feeds/handler", new SetPropertiesRule(new String[] {"class"}, new String[] {"clazz"}));
		digester.addSetNext("feeds/handler", "addHandler");
		
		digester.addObjectCreate("feeds/handler/property", Property.class);
		digester.addSetProperties("feeds/handler/property");
		digester.addSetNext("feeds/handler/property", "addProperty");
		
		digester.addObjectCreate("feeds/feed", Feed.class);
		digester.addSetProperties("feeds/feed");
		digester.addSetNext("feeds/feed", "addFeed");
		
		digester.addObjectCreate("feeds/feed/filter", Filter.class);
		digester.addSetProperties("feeds/feed/filter");
		digester.addRule("feeds/feed/filter", new SetPropertiesRule(new String[] {"class"}, new String[] {"clazz"}));
		digester.addSetNext("feeds/feed/filter", "addFilter");
		
		digester.addObjectCreate("feeds/feed/handler", Handler.class);
		digester.addSetProperties("feeds/feed/handler");
		digester.addRule("feeds/feed/handler", new SetPropertiesRule(new String[] {"class"}, new String[] {"clazz"}));
		digester.addSetNext("feeds/feed/handler", "addHandler");
		
		digester.addObjectCreate("feeds/feed/handler/property", Property.class);
		digester.addSetProperties("feeds/feed/handler/property");
		digester.addSetNext("feeds/feed/handler/property", "addProperty");
		
		return digester;
	}
	
	public static Feeds parse(File file) throws ConfigParserException {
		try {
			return (Feeds) getDigester().parse(file);
		}
		catch (Exception e) {
			throw new ConfigParserException("Failed to parse config at " + file, e);
		}
	}
}
