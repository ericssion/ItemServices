package com.item.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.item.bean.MailBean;


public final class MailMethods
{
	

	private MailMethods() {
	}

	public static void sendStatusMail(MailBean mailBean)
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
		
		message.setSubject(PropertyUtils.getMailStatusSubject());
		 
		MimeBodyPart body = new MimeBodyPart();
		body.setText(statusMailBody(mailBean));
		
		MimeBodyPart logfile = new MimeBodyPart();
		
		
		try {
			FileUtils.createFile(mailBean);
			logfile.attachFile(FileUtils.getfilePath());
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);
			multipart.addBodyPart(logfile);
			message.setContent(multipart);
			Transport.send(message);
			FileUtils.deleteFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String statusMailBody(MailBean mailBean) {
		
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
		
		String msg="The KaiNexus Person API returned errors for the batch initiated "+timeStamp+".\r\n"
		+ "Input file: "+mailBean.getFileName()+"\r\n"
		+ "Records Created or Updated: "+mailBean.getSucessRcdCnt()+"\r\n"
		+ "Records with Errors:"+mailBean.getErrorRcdcnt()+"\r\n"
		+ "Error List: See the attached error log file";
		return msg;
	}
	
	private static String fatalErrorBody(String fileName) {
		
		return "The submitted file is not correctly formatted for use by the KaiNexus Person API: "+fileName+".";
		
	}
	

	public static void sendGMail(MailBean mailBean)
		    throws MessagingException{
		
		final String username = "prasad.javastuff@gmail.com";
        final String password = "Gudavalli_21";
		
		Properties prop = new Properties();
		
		   prop.put("mail.smtp.host", "smtp.gmail.com");
	        prop.put("mail.smtp.port", "587");
	        prop.put("mail.smtp.auth", "true");
	        prop.put("mail.smtp.starttls.enable", "true"); //TLS
	        
	        Session session = Session.getInstance(prop,
	                new javax.mail.Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication(username, password);
	                    }
	                });
		
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("prasad.javastuff@gmail.com"));
		message.setRecipients(
		  Message.RecipientType.TO, InternetAddress.parse("prasadbabugudavalli@gmail.com"));
		
		message.setSubject(PropertyUtils.getMailStatusSubject());
		 
		MimeBodyPart body = new MimeBodyPart();
		body.setText(statusMailBody(mailBean));
		
		MimeBodyPart logfile = new MimeBodyPart();
		
		
		try {
			FileUtils.createFile(mailBean);
			logfile.attachFile(FileUtils.getfilePath());
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);
			multipart.addBodyPart(logfile);
			message.setContent(multipart);
			Transport.send(message);
			FileUtils.deleteFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		message.setSubject(PropertyUtils.getMailStatusSubject());
		 
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(asMessage, "text/html");
		 
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		 
		message.setContent(multipart);
		 
		Transport.send(message);
	}
	
	public static void sendFatalMail(String fileName)
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
		
		message.setSubject(PropertyUtils.getMailFatalSubject());
		 
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(fatalErrorBody(fileName	), "text/html");
		 
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		 
		message.setContent(multipart);
		 
		Transport.send(message);
	}
	
	
	public static void sendGMail(String fileName)
		    throws MessagingException{
		
		final String username = "prasad.javastuff@gmail.com";
        final String password = "Gudavalli_21";
		
		Properties prop = new Properties();
		
		   prop.put("mail.smtp.host", "smtp.gmail.com");
	        prop.put("mail.smtp.port", "587");
	        prop.put("mail.smtp.auth", "true");
	        prop.put("mail.smtp.starttls.enable", "true"); //TLS
	        
	        Session session = Session.getInstance(prop,
	                new javax.mail.Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication(username, password);
	                    }
	                });
		
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("prasad.javastuff@gmail.com"));
		message.setRecipients(
		  Message.RecipientType.TO, InternetAddress.parse("prasadbabugudavalli@gmail.com"));
		
		message.setSubject(PropertyUtils.getMailFatalSubject());
		 
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(fatalErrorBody(fileName	), "text/html");
		 
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		 
		message.setContent(multipart);
		 
		Transport.send(message);
		
	}
}
