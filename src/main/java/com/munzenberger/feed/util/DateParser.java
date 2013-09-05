package com.munzenberger.feed.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.format.ISODateTimeFormat;

import com.munzenberger.feed.log.Logger;

public class DateParser {

	public static Date parse(String source) {
		return parse(source, null);
	}
	
	public static Date parse(String source, Logger logger) {
		if (source == null) {
			return null;
		}
		
		try {
			DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");			
			return format.parse(source);
		}
		catch (ParseException e) {
			// try again below
		}
		
		try {
			long millis = ISODateTimeFormat.dateTime().parseMillis(source);
			return new Date(millis);
		}
		catch (Exception e) {
			// try again below
		}
		
		try {
			long millis = ISODateTimeFormat.dateTimeNoMillis().parseMillis(source);
			return new Date(millis);
		}
		catch (Exception e) {
		}
		
		if (logger != null) {
			logger.info("Unparsable date: " + source);
		}
		
		return null;
	}
}
