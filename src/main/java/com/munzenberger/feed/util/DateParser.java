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
			logger.log("Unparsable date: " + source);
		}

		return null;
	}
}
