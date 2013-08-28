package com.munzenberger.feed.handler;

import org.apache.commons.mail.HtmlEmail;

import junit.framework.TestCase;

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
		
		System.out.println(message);
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
}
