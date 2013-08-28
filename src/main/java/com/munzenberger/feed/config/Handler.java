package com.munzenberger.feed.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Handler {

	private String clazz;
	
	private String name;
	
	private String ref;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
}
