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
package com.munzenberger.feed.handler;

import org.apache.commons.mail.HtmlEmail;

import junit.framework.TestCase;

import com.munzenberger.feed.handler.SendEmail.MailItem;
import com.munzenberger.feed.parser.rss.Channel;
import com.munzenberger.feed.parser.rss.Enclosure;
import com.munzenberger.feed.parser.rss.Item;

public class SendEmailTest extends TestCase {

	public void testGetHtmlMsg() {

		SendEmail handler = new SendEmail();

		Item item = new Item();
		item.setDescription("Hello world!");
		item.setLink("http://www.google.com");

		Enclosure e = new Enclosure();
		e.setUrl("http://download_one.com");
		item.addEnclosure(e);

		e = new Enclosure();
		e.setUrl("http://download_two.com");
		item.addEnclosure(e);

		String message = handler.getHtmlMsg(item);
		assertNotNull(message);
		assertTrue(message.contains("Hello world!"));
		assertTrue(message.contains("http://www.google.com"));
		assertTrue(message.contains("http://download_one.com"));
		assertTrue(message.contains("http://download_two.com"));
	}

	public void testSetFrom() throws Exception {

		HtmlEmail email = new HtmlEmail();

		Item item = new Item();
		item.setChannel(new Channel());
		item.getChannel().setTitle("Title");

		SendEmail handler = new SendEmail();

		handler.setFrom(email, item);
		assertEquals("no@reply.com", email.getFromAddress().getAddress());
		assertEquals("Title", email.getFromAddress().getPersonal());

		item.setAuthor("invalid");

		handler.setFrom(email, item);
		assertEquals("no@reply.com", email.getFromAddress().getAddress());
		assertEquals("Title", email.getFromAddress().getPersonal());

		item.setAuthor("test@email.com");

		handler.setFrom(email, item);
		assertEquals("test@email.com", email.getFromAddress().getAddress());
		assertEquals("Title", email.getFromAddress().getPersonal());
	}

	public void testDescriptionEntityEncoded() {

		Item item = new Item();
		item.setDescription("Hello");

		MailItem mailItem = new MailItem(item);

		String encoded = mailItem.getDescription();
		assertEquals("Hello", encoded);

		item.setDescription("\u00ae");
		mailItem = new MailItem(item);
		encoded = mailItem.getDescription();
		assertEquals("&#0174;", encoded);
	}
}
