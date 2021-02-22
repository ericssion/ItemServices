/**
 * This is the util class used to handle the load the files in a specific path
 * in the file system  
 * 
 * @author prasad
 */

package com.item.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.item.request.Request;


/**
 * The Class FileUtils.
 */
public class FileUtils {

	private static Logger log = LoggerFactory.getLogger(FileUtils.class);
	/**
	 * Load files.
	 *
	 * @return the list
	 */
	public static List<Request> loadFiles() {
		
		List<Request> requestList=new ArrayList<Request>();
		log.debug("entered into FileUtils.loadFiles()");
		ObjectMapper mapper = new ObjectMapper();
		
		Request request=null;
		
		BufferedReader reader=null;
			
			List<String> filesInFolder=FtpUtils.getFilesFromFtpServer();
			for(String filePath : filesInFolder) {
				String line;
				log.info("loding the files from the Directory");
				
				try {
					reader = Files.newBufferedReader(Paths.get(filePath));
					
					 while ((line = reader.readLine()) != null) {
						 request=mapper.readValue(line, Request.class);
						 requestList.add(request);
					    }

				} catch (IOException e) {
					 log.error("not able to read and parse the files from the "+filePath+" directory "+e.getMessage(),e);
					 try {
					  		MailMethods.sendMail("not able to read and parse the files", "not able to read and parse the files from the "+filePath+" directory "+e.getMessage());
					  	} catch (Exception ex) {
					  		log.error("Failed to send Mail "+ex.getMessage(),ex);
						}
				}
					
			}
			
			log.debug("exiting into FileUtils.loadFiles()");
		return requestList;
		
	}
    
}
