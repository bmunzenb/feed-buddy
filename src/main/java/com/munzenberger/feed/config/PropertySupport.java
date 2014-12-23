package com.munzenberger.feed.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class PropertySupport {

	private String clazz;
	
	private final Map<String,String> properties = new HashMap<String,String>();
	
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	public void addProperty(Property p) {
		properties.put(p.getName(), p.getValue());
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	public Map<String,String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}
}
