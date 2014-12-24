package com.munzenberger.feed.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
		
		digestProperties(digester, "feeds/handler/property");
		
		digester.addObjectCreate("feeds/feed", Feed.class);
		digester.addSetProperties("feeds/feed");
		digester.addSetNext("feeds/feed", "addFeed");
		
		digester.addObjectCreate("feeds/feed/filter", Filter.class);
		digester.addSetProperties("feeds/feed/filter");
		digester.addRule("feeds/feed/filter", new SetPropertiesRule(new String[] {"class"}, new String[] {"clazz"}));
		digester.addSetNext("feeds/feed/filter", "addFilter");
		
		digestProperties(digester, "feeds/feed/filter/property");
				
		digester.addObjectCreate("feeds/feed/handler", Handler.class);
		digester.addSetProperties("feeds/feed/handler");
		digester.addRule("feeds/feed/handler", new SetPropertiesRule(new String[] {"class"}, new String[] {"clazz"}));
		digester.addSetNext("feeds/feed/handler", "addHandler");
		
		digestProperties(digester, "feeds/feed/handler/property");
		
		return digester;
	}
	
	protected static void digestProperties(Digester digester, String pattern) {
		digester.addObjectCreate(pattern, Property.class);
		digester.addSetProperties(pattern);
		digester.addSetNext(pattern, "addProperty");
	}
	
	public static Feeds parse(File file) throws ConfigParserException {
		try {
			FileInputStream in = new FileInputStream(file);
			return parse(in);
		}
		catch (FileNotFoundException e) {
			throw new ConfigParserException("Configuration file not found: " + file, e);
		}
	}
	
	public static Feeds parse(InputStream in) throws ConfigParserException {
		try {
			return (Feeds) getDigester().parse(in);
		}
		catch (Exception e) {
			throw new ConfigParserException("Failed to parse config stream", e);
		}
	}
}
