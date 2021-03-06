/**
 * This is the util class used to handle the load the files in a specific path
 * in the file system  
 * 
 * @author prasad
 */

package com.item.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.item.bean.FileBean;
import com.item.bean.MailBean;


/**
 * The Class FileUtils.
 */
public class FileUtils {

	private static Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	private static String filePath="";
	/**
	 * Load files.
	 *
	 * @return the list
	 */
	public static Map<String,List<String>> loadFiles() {
		
		Map<String,List<String>> response=new HashMap<String,List<String>>();
		
		log.debug("entered into FileUtils.loadFiles()");
		
		BufferedReader reader=null;
		boolean fatalError=false; 	
			List<FileBean> filesInFolder=FtpUtils.getFilesFromFtpServer();
			for(FileBean fileBean : filesInFolder) {
				List<String> requestList=new ArrayList<String>();
				
				String line;
				log.info("loding the files from the Directory");
				
				try {
					
					reader = Files.newBufferedReader(Paths.get(fileBean.getFilepath()));
					
					 while ((line = reader.readLine()) != null) {
						 
						// request=mapper.readValue(line, Request.class);
						 if(isValidJSON(line)) {
							 requestList.add(line);
						 }else {
							 fatalError=true;
							 break;
						 }
						 
					    }
					 
					 if(fatalError) {
						 try {
						  		MailMethods.sendFatalMail(fileBean.getFilename());
						  		response.clear();
						  		break;
						  	} catch (Exception ex) {
						  		log.error("Failed to send Mail "+ex.getMessage(),ex);
							}
					 }
					 
					 

				} catch (IOException e) {
					 log.error("not able to read and parse the files from the "+fileBean.getFilepath()+" directory "+e.getMessage(),e);
					 try {
					  		MailMethods.sendFatalMail(fileBean.getFilename());
					  		response.clear();
					  		break;
					  	} catch (Exception ex) {
					  		log.error("Failed to send Mail "+ex.getMessage(),ex);
						}
				}
				response.put(fileBean.getFilename(), requestList);
			}
			
			log.debug("exiting into FileUtils.loadFiles()");
		return response;
		
	}
    
	
	 public static boolean isValidJSON(String line ) {
		    try {
		       final ObjectMapper mapper = new ObjectMapper();
		       mapper.readTree(line);
		       return true;
		    } catch (IOException e) {
		       return false;
		    }
		  }
	
	public static String getfilePath() {
		return filePath;
	}
	
	public static void createFile(MailBean mailBean) {
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		filePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString()+"\\KaiNexus_Error(s)_"+timeStamp+".log";
		
		System.out.println(" File Path :"+filePath);
		StringBuffer buffer=new StringBuffer();
		
		mailBean.getErrorMap().forEach((request,response)->{
			buffer.append("Request : "+request +" ---> Response : "+response+" ;");
			
			buffer.append(System.lineSeparator());
		});
		Writer out=null;
		
			try {
				 out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
				
			    out.write(buffer.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			    try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	public static  boolean deleteFile() throws Exception {
		return 	Files.deleteIfExists(Paths.get(filePath));
	}
	
}
