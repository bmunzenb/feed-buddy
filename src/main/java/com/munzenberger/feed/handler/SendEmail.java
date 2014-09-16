package com.munzenberger.feed.handler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;
import java.util.List;

import javax.mail.Session;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Enclosure;
import com.munzenberger.feed.parser.rss.Item;
import com.munzenberger.feed.util.DateParser;

public class SendEmail implements ItemHandler {

	private String to;
	private String from;
	private String smtpHost;
	private String smtpPort;
	private String auth = "";
	private String username = "";
	private String password = "";
	private String startTLSEnable = "false";
	private String startTLSRequired = "false";

	public void setTo(String to) {
		this.to = to;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
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
	
	public void setStartTLSEnable(String startTLSEnable) {
		this.startTLSEnable = startTLSEnable;
	}

	public void setStartTLSRequired(String startTLSRequired) {
		this.startTLSRequired = startTLSRequired;
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
		MailItem mailItem = new MailItem(item);		
		Reader reader = new InputStreamReader( getClass().getResourceAsStream("SendEmail.vm") );
		VelocityContext context = new VelocityContext();
		context.put("item", mailItem);
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
			props.put("mail.smtp.starttls.enable", this.startTLSEnable);
			props.put("mail.smtp.starttls.required", this.startTLSRequired);
			
			mailSession = Session.getInstance(props);
		}
		return mailSession;
	}
	
	public static class MailItem {
		
		private final Item item;
				
		public MailItem(Item item) {
			this.item = item;
		}
		
		public String getDescription() {
			
			StringBuilder encoded = new StringBuilder();
			
			if (item.getDescription() != null) {
				DecimalFormat format = new DecimalFormat("0000");
				for (int i = 0; i < item.getDescription().length(); i++) {
					char c = item.getDescription().charAt(i);
					if (c >= 0x7f) {
						encoded.append("&#" + format.format(c) + ";");
					}
					else {
						encoded.append(c);
					}
				}
			}

			return encoded.toString();
		}
		
		public String getLink() {
			return item.getLink();
		}
		
		public List<Enclosure> getEnclosures() {
			return item.getEnclosures();
		}
	}
}