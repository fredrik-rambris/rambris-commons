package com.rambris;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.smtp.SMTPTransport;

/**
 * Make sending emails easy.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: Message.java 27 2010-05-23 18:48:54Z boost $
 * 
 */
public class Message extends SMTPMessage
{
	Properties config = null;
	Session session = null;
	SMTPTransport transport = null;
	private String messageId = null;
	public boolean appendUid = true;
	public boolean appendHost = true;
	private static String localhost = null;

	private static Session getNewSession(Properties config)
	{
		if (!config.containsKey("mail.transport.protocol")) config.setProperty("mail.transport.protocol", "smtp");
		if (!config.containsKey("mail.mime.charset")) config.setProperty("mail.mime.charset", "iso-8859-1");
		return Session.getDefaultInstance(config);
	}

	public Message(Properties config) throws MessagingException
	{
		super(getNewSession(config));
		this.config = config;
		session = getNewSession(config);
	}

	public Message(Properties config, String from, String to, String subject) throws MessagingException
	{
		this(config);
		this.setFrom(new InternetAddress(from));
		this.setRecipient(RecipientType.TO, new InternetAddress(to));
		this.setSubject(subject);
	}

	@Override
	public void setSubject(String subject) throws MessagingException
	{
		// TODO Auto-generated method stub
		try
		{
			super.setSubject(MimeUtility.encodeText(subject, session.getProperty("mail.mime.charset"), "Q"));
		}
		catch (UnsupportedEncodingException e)
		{
			super.setSubject(subject);
		}
	}

	private void connect() throws MessagingException
	{
		synchronized (transport)
		{
			if (transport == null)
			{
				transport = (SMTPTransport) session.getTransport("smtp");
				transport.connect(config.getProperty("address"), config.getProperty("username"), config
						.getProperty("password"));
			}
		}
	}

	public void setFrom(String email) throws MessagingException
	{
		super.setFrom(new InternetAddress(email));
	}

	public void setFrom(String email, String name) throws MessagingException, UnsupportedEncodingException
	{
		super.setFrom(new InternetAddress(email, name, session.getProperty("mail.mime.charset")));
	}

	public void addTo(String email) throws MessagingException
	{
		super.addRecipient(RecipientType.TO, new InternetAddress(email));
	}

	public void addTo(String email, String name) throws MessagingException, UnsupportedEncodingException
	{
		if (name == null) addTo(email);
		else super.addRecipient(RecipientType.TO, new InternetAddress(email, name, session
				.getProperty("mail.mime.charset")));
	}

	public void addCC(String email) throws MessagingException
	{
		super.addRecipient(RecipientType.CC, new InternetAddress(email));
	}

	public void addCC(String email, String name) throws MessagingException, UnsupportedEncodingException
	{
		if (name == null) addCC(email);
		else super.addRecipient(RecipientType.CC, new InternetAddress(email, name, session
				.getProperty("mail.mime.charset")));
	}

	public void addBCC(String email) throws MessagingException
	{
		super.addRecipient(RecipientType.BCC, new InternetAddress(email));
	}

	public void addBCC(String email, String name) throws MessagingException, UnsupportedEncodingException
	{
		if (name == null) addBCC(email);
		else super.addRecipient(RecipientType.BCC, new InternetAddress(email, name, session
				.getProperty("mail.mime.charset")));
	}

	public Address[] getRecipients() throws MessagingException
	{
		ArrayList<Address> recipients = new ArrayList<Address>();
		if (getRecipients(RecipientType.TO) != null)
		{
			for (Address recipient : getRecipients(RecipientType.TO))
				recipients.add(recipient);
		}
		if (getRecipients(RecipientType.CC) != null)
		{
			for (Address recipient : getRecipients(RecipientType.CC))
				recipients.add(recipient);
		}
		if (getRecipients(RecipientType.BCC) != null)
		{
			for (Address recipient : getRecipients(RecipientType.BCC))
				recipients.add(recipient);
		}
		return recipients.toArray(new Address[recipients.size()]);
	}

	public void clearRecipients() throws MessagingException
	{
		super.setRecipients(RecipientType.TO, (Address[]) null);
		super.setRecipients(RecipientType.CC, (Address[]) null);
		super.setRecipients(RecipientType.BCC, (Address[]) null);

	}

	public void close()
	{
		synchronized (transport)
		{
			if (transport != null)
			{
				try
				{
					transport.close();
				}
				catch (MessagingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				transport = null;
			}
		}
	}

	public void setContent(String text_body, String html_body) throws MessagingException
	{
		MimeMultipart multipart = new MimeMultipart("alternative");
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(text_body, "text/plain;charset=" + session.getProperty("mail.mime.charset"));
		multipart.addBodyPart(messageBodyPart);
		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(html_body, "text/html;charset=" + session.getProperty("mail.mime.charset"));
		multipart.addBodyPart(messageBodyPart);
		super.setContent(multipart);
	}

	public void setDate(Date date) throws MessagingException
	{
		if (date == null) removeHeader("Date");
		setHeader("Date", Util.getDateAsRFC822String(date));
	}

	public int send() throws MessagingException
	{
		connect();
		transport.sendMessage(this, getRecipients());
		return getLastReturnCode();
	}

	public int getLastReturnCode()
	{
		return transport.getLastReturnCode();
	}

	public String getLastServerResponse()
	{
		return transport.getLastServerResponse();
	}

	@Override
	protected void updateMessageID() throws MessagingException
	{
		if (messageId != null) setHeader("Message-ID", "<" + messageId + (appendUid ? generateUid() + "." : "")
				+ (appendHost ? "@" + getLocalhost() : "") + ">");
		else super.updateMessageID();
	}

	protected String generateUid()
	{
		long now = new Date().getTime() * (long) (Math.random() * System.nanoTime());
		return String.format("%x", now);
	}

	public void setMessageId(String messageId)
	{
		this.messageId = messageId;
	}

	protected synchronized String getLocalhost()
	{
		if (localhost == null)
		{
			localhost = config.getProperty("mail.smtp.localhost", session.getProperty("mail.smtp.localhost"));
			if (localhost == null)
			{
				try
				{
					localhost = InetAddress.getLocalHost().getHostName();
				}
				catch (UnknownHostException e)
				{
					localhost = "localhost.localdomain";
				}
			}
		}
		return localhost;
	}

	public Message(Session session)
	{
		super(session);
	}
}
