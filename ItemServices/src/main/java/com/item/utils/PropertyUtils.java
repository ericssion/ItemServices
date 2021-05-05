package com.item.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.item.controller.ItemController;

public final class PropertyUtils {

	private PropertyUtils() {
		
	}
	
	private static Logger LOG = LoggerFactory.getLogger(ItemController.class);

	private static Properties itemServicesProperties;
	
	public static Properties loadProperty() {
		    //to load application's properties, we use this class
		
		  if( null==itemServicesProperties) {
			  
			  synchronized (Properties.class) { if (itemServicesProperties == null) { // Double checked _instance = new Singleton(); 
				  
				  itemServicesProperties = new Properties();

				    FileInputStream file;

				    //the base folder is ./, the root of the main.properties file  
				    String path = "./itemServices.properties";

				    //load the file handle for main.properties
				    try {
						file = new FileInputStream(path);
						itemServicesProperties.load(file);
						file.close();
					} catch (FileNotFoundException e) {
						LOG.debug("Failed to Load the clinicalServices.properties File");	
					} catch (IOException e) {
						LOG.debug("Failed to Read the clinicalServices.properties File");
					}			  
			  
			  }
			  }

		  }
		    return itemServicesProperties;
	}

	
	public static int getFrequency() {
		Properties props= PropertyUtils.loadProperty();
		return Integer.parseInt(props.getProperty("app.frequency"));
	}
	
	
	public static String getMailFrom() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("mail.from");
	}
	
	public static String getMailTo() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("mail.to");
	}
	
	public static String getMailSubject() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("userlist.mail.subject");
	}
	
	public static String getMailbody() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("userlist.mail.body");
	}
	

	public static String getRemoteInDirectory() {
		Properties props= 	PropertyUtils.loadProperty();
		
		return props.getProperty("remote.in");
	}
	
	public static String getRemoteoutDirectory() {
		Properties props= 	PropertyUtils.loadProperty();
		
		return props.getProperty("remote.out");
	}
	
	public static String getRemoteServer() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("remote.server");
	}
	
	
	public static String getRemoteKey() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("remote.key");
	}
	
	
	public static String getRemoteUser() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("remote.user");
	}
	
	
	public static String getApiUser() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("items.user");
	}
	
	public static String getApiKey() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("items.key");
	}
	
	public static String getApiUrl() {
		Properties props= PropertyUtils.loadProperty();
		return props.getProperty("items.url");
	}
	
	
	public static String getUserListApiUrl() {
		Properties props= PropertyUtils.loadProperty();
		return props.getProperty("userlist.url");
	}
	
	
	public static String getMailStatusSubject() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("mail.status");
	}
	
	public static String getMailFatalSubject() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("mail.fatal");
	}
	
	
	public static String getMailCC() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("mail.cc");
	}
	
	public static String getUserListMailSubject() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("userlist.mail.subject");
	}
	
	public static String getUserListMailBody() {
		Properties props= 	PropertyUtils.loadProperty();
		return props.getProperty("userlist.mail.body");
	}
	
	
	public static String getBackupDir() {
		Properties props=PropertyUtils.loadProperty();
		return props.getProperty("backup.dir");
	}
}
