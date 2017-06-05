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
package com.munzenberger.feed.parser.rss;

import java.io.Reader;

import org.apache.commons.digester.Digester;

import com.munzenberger.feed.parser.AbstractParser;

public class RSSParser extends AbstractParser {

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
		digester.addCallMethod("rss/channel/item/category", "addCategory", 1);
		digester.addCallParam("rss/channel/item/category", 0);
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

	@Override
	protected RSS parse(Reader in) throws Exception {
		return (RSS) getDigester().parse(in);
	}
}
