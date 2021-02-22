package com.item.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FtpUtils {

  private static Logger log = LoggerFactory.getLogger(FtpUtils.class);
  private static JSch stpsClient;
  private static FTPClient ftpClient;

  private static JSch loadSftpProperty() {

    if (null == stpsClient) {

      synchronized(JSch.class) {
        if (stpsClient == null) { // Double checked _instance = new Singleton(); 
          stpsClient = new JSch();
        }
      }
    }
    return stpsClient;
  }

  private static FTPClient loadFtpProperty() {

    if (null == ftpClient) {

      synchronized(FTPClient.class) {
        if (ftpClient == null) { // Double checked _instance = new Singleton(); 
          ftpClient = new FTPClient();
        }
      }
    }
    return ftpClient;
  }

  public static List < String > getFilesFromFtpServer() {
	  log.debug("entered into FtpUtils.getFilesFromFtpServer()");
	  
    loadFtpProperty();
    int reply;
    List < String > fileList = new ArrayList < String > ();

    try {
     log.info("Connecting to  server " + PropertyUtils.getRemoteServer() + "....");
         	
      ftpClient.connect(PropertyUtils.getRemoteServer(), 21);

      log.info("Connected to Server .");
     
      reply = ftpClient.getReplyCode();

      if (!FTPReply.isPositiveCompletion(reply)) {
        ftpClient.disconnect();
        log.error("FTP server refused connection.");

        String asSubject = "Item Service FTP Server Issue ";
        String asMessage = "FTP server refused connection";
        try {
          MailMethods.sendMail(asSubject, asMessage);
        } catch(Exception e) {
          log.error("Failed to send Mail "+e.getMessage(),  e);
        }

        System.exit(1);
      }
      log.info("login with user " + PropertyUtils.getRemoteUser() + ".");
      ftpClient.login(PropertyUtils.getRemoteUser(), PropertyUtils.getRemoteKey());
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      ftpClient.changeWorkingDirectory(PropertyUtils.getRemoteDirectory());
      
      FTPFile[] files = ftpClient.listFiles();
      if(files.length==0)
      log.info("No files are there to process in FTP Server : " + PropertyUtils.getRemoteServer() + ".");
      
      for (FTPFile ftpFile: files) {

        if (ftpFile.getType() == FTPFile.FILE_TYPE) {
        	
          log.info("copying files from  server : " + ftpFile.getName()+" to "+PropertyUtils.getBackupDir() + ftpFile.getName());
          OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(PropertyUtils.getBackupDir() + ftpFile.getName()));
          boolean success = ftpClient.retrieveFile(ftpFile.getName(), outputStream);
          outputStream.close();
          //file copy success add to file list and  delete the file
			  if (success) { 
				  log.info("copying sucessful");
				  fileList.add(PropertyUtils.getBackupDir() +ftpFile.getName());
			  Boolean deletestatus= ftpClient.deleteFile(PropertyUtils.getRemoteDirectory()+ftpFile.getName());
			  if(deletestatus) {
			  log.info("delete sucessful");
			  }else {
				  try {
			            MailMethods.sendMail("unable to delete the file from FTP Server", "unable to delete the file from FTP Server from "+PropertyUtils.getRemoteDirectory()+ftpFile.getName());
			          } catch(Exception e) {
			            log.error("Failed to send Mail " + e);
			          }
			  }
			  }
        }
      }
      ftpClient.logout();
      log.info("logout from server");
    } catch(IOException e) {
      log.error("not able to copy file from server "+e.getMessage(),e);
      try {
          MailMethods.sendMail("not able to copy file from FTP server", "not able to copy file from FTP server"+e.getMessage());
        } catch(Exception e1) {
          log.error("Failed to send Mail " + e1);
        }
    } finally {
      if (ftpClient.isConnected()) {
        try {
          ftpClient.disconnect();
          log.info(" server Disconnected ");
        } catch(IOException ioe) {
          String asSubject = "Item Service FTP Server disconnect Issue ";
          String asMessage = "not able to disconnect from FTP server";
          try {
            MailMethods.sendMail(asSubject, asMessage);
          } catch(Exception e) {
            log.error("Failed to send Mail " + e);
          }
        }
      }
    }
    
    log.debug("exiting into FtpUtils.getFilesFromFtpServer()");
    return fileList;
  }

  @SuppressWarnings("unchecked")
  public static List < String > getFilesFromSftpServer() {

    List < String > filesList = new ArrayList < String > ();

    loadSftpProperty();
    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    Session jschSession = null;
    ChannelSftp channelSftp = null;
    try {
      jschSession = stpsClient.getSession(PropertyUtils.getRemoteUser(), PropertyUtils.getRemoteServer());

      jschSession.setPassword(PropertyUtils.getRemoteKey());
      jschSession.setConfig(config);
      jschSession.connect();
      channelSftp = (ChannelSftp) jschSession.openChannel("sftp");

      channelSftp.connect();
      channelSftp.cd(PropertyUtils.getRemoteDirectory());
      Vector < ChannelSftp.LsEntry > list = channelSftp.ls("*.json");

      for (ChannelSftp.LsEntry entry: list) {
        System.out.println(entry.getFilename());
        filesList.add(PropertyUtils.getBackupDir() + entry.getFilename());
        channelSftp.get(entry.getFilename(), PropertyUtils.getBackupDir() + entry.getFilename());
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new
        BufferedInputStream(channelSftp.get(entry.getFilename()));
        File newFile = new File(PropertyUtils.getBackupDir() + entry.getFilename());
        OutputStream os = new FileOutputStream(newFile);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        int readCount;
        System.out.println("Writing: " + entry.getFilename());
        while ((readCount = bis.read(buffer)) > 0) {
          bos.write(buffer, 0, readCount);
        }
        bis.close();
        bos.close();

      }

    } catch(JSchException | SftpException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      channelSftp.exit();
      jschSession.disconnect();
    }
    return filesList;
  }

}