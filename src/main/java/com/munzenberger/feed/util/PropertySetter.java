package com.munzenberger.feed.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.munzenberger.feed.config.PropertySupport;

public final class PropertySetter {

	public static <T> void setProperties(Class<T> clazz, T target, PropertySupport properties) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				
		for (Map.Entry<String,String> e : properties.getProperties().entrySet()) {
			String name = e.getKey();
			name = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			
			Method setter = clazz.getMethod(name, String.class);
			setter.invoke(target, e.getValue());
		}
	}
}
