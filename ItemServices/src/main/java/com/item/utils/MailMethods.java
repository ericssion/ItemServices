package com.item.utils;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public final class MailMethods
{
	

	private MailMethods() {
		
	}

	public static void sendMail(String asSubject, String asMessage)
		    throws MessagingException{
		
		String asFrom=PropertyUtils.getMailFrom();
	 	String asTo=PropertyUtils.getMailTo();
	 	String asCC=PropertyUtils.getMailCC();
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "relay.umassmemorial.org");
		
		
		Session session = Session.getDefaultInstance(prop);  
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(asFrom));
		message.setRecipients(
		  Message.RecipientType.TO, InternetAddress.parse(asTo));
		message.setRecipients(
				  Message.RecipientType.CC, InternetAddress.parse(asCC));
		
		message.setSubject(PropertyUtils.getMailSubject());
		 
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(asMessage, "text/html");
		 
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		 
		message.setContent(multipart);
		 
		Transport.send(message);
	}
	

}
