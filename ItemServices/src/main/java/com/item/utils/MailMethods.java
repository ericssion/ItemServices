package com.item.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
		MimeBodyPart body = new MimeBodyPart();
		message.setFrom(new InternetAddress(asFrom));
		message.setRecipients(
		  Message.RecipientType.TO, InternetAddress.parse(asTo));
		message.setRecipients(
				  Message.RecipientType.CC, InternetAddress.parse(asCC));
		
		if(mailBean.getErrorRcdcnt()>0) {
			message.setSubject(PropertyUtils.getMailErrorSubject());
			body.setText(errorMailBody(mailBean));
		}else {
			message.setSubject(PropertyUtils.getMailSucessSubject());
			body.setText(sucessMailBody(mailBean));
		}
		
		MimeBodyPart logfile = new MimeBodyPart();
		
		try {
			FileUtils.createFile(mailBean);
			Multipart multipart = new MimeMultipart();
			if(mailBean.getErrorRcdcnt()>0) {
				logfile.attachFile(FileUtils.getfilePath());
				multipart.addBodyPart(logfile);
			}
			multipart.addBodyPart(body);
			message.setContent(multipart);
			Transport.send(message);
			FileUtils.deleteFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String sucessMailBody(MailBean mailBean) {
		
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(FtpUtils.getFileTimeStamp());
		
		String msg="The KaiNexus Person API import was successful for the batch initiated "+timeStamp+".\r\n"
		+ "Input file: "+mailBean.getFileName()+"\r\n"
		+ "Records Created or Updated: "+mailBean.getSucessRcdCnt()+"\r\n";
		return msg;
	}
	
	private static String errorMailBody(MailBean mailBean) {
		
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(FtpUtils.getFileTimeStamp());
		
		String msg="The KaiNexus Person API returned errors for the batch initiated "+timeStamp+".\r\n"
		+ "Input file: "+mailBean.getFileName()+"\r\n"
		+ "Records Created or Updated: "+mailBean.getSucessRcdCnt()+"\r\n"
		+ "Records with Errors: "+mailBean.getErrorRcdcnt()+"\r\n"
		+ "Error List: See the attached error log file";
		return msg;
	}
	
	private static String fatalErrorBody(String fileName) {
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		
		
		return "The submitted file is not correctly formatted for use by the KaiNexus Person API: "+fileName+"_"+timeStamp+".";
		
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
		
		message.setSubject(asSubject);
		 
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
	
	
}
