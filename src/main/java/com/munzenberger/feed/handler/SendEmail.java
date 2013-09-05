package com.munzenberger.feed.handler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.mail.Session;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

public class SendEmail implements ItemHandler {

	private String to;
	
	private String from;
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	private String smtpHost;
	
	private String smtpPort;
	
	private String auth = "";
	
	private String username = "";
	
	private String password = "";
	
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	
	public void setAuth(String auth) {
		this.auth = auth;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void process(Item item, Logger logger) throws ItemHandlerException {
		
		HtmlEmail email = new HtmlEmail();
		
		try {
			email.addTo(to);
			setFrom(email, item);
			setSentDate(email, item, logger);
			email.setSubject(item.getTitle());
			email.setHtmlMsg(getHtmlMsg(item));
			email.setMailSession(getMailSession());
			email.send();
		}
		catch (Exception e) {
			throw new ItemHandlerException("Could not send email", e);
		}
		
		logger.info("Sent item email to " + to);
	}
	
	protected void setFrom(HtmlEmail email, Item item) throws EmailException {
		if (this.from != null) {
			try {
				email.setFrom(this.from, item.getChannel().getTitle());
				return;
			} catch (EmailException e) {
			}
		}
		
		if (item.getAuthor() != null) {
			try {
				email.setFrom(item.getAuthor(), item.getChannel().getTitle());
				return;
			} catch (EmailException e) {
			}
		}
		
		email.setFrom("no@reply.com", item.getChannel().getTitle());
	}
	
	protected void setSentDate(HtmlEmail email, Item item, Logger logger) {
		if (item.getPubDate() != null) {
			Date d = DateParser.parse(item.getPubDate(), logger);
			if (d != null) {
				email.setSentDate(d);
			}
		}
	}
	
	protected String getHtmlMsg(Item item) {
		Reader reader = new InputStreamReader( getClass().getResourceAsStream("SendEmail.vm") );
		VelocityContext context = new VelocityContext();
		context.put("item", item);
		StringWriter writer = new StringWriter();
		Velocity.evaluate(context, writer, "", reader);
		return writer.toString();
	}
	
	private Session mailSession;
	
	protected Session getMailSession() {
		if (mailSession == null) {
			
			Properties props = new Properties();
			props.put("mail.smtp.host", this.smtpHost);
			props.put("mail.smtp.port", this.smtpPort);
			props.put("mail.smtp.auth", this.auth);
			props.put("mail.smtp.user", this.username);
			props.put("mail.smtp.password", this.password);
			
			mailSession = Session.getInstance(props);
		}
		return mailSession;
	}
}
