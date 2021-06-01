
package com.item.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.item.bean.MailBean;
import com.item.request.JsonResponse;
import com.item.utils.FileUtils;
import com.item.utils.FtpUtils;
import com.item.utils.MailMethods;
import com.item.utils.PropertyUtils;

/**
 * The Class ProcessPatient.
 */
public class ProcessItems {

	private static Logger log = LoggerFactory.getLogger(ProcessItems.class);
	int sucessRcdCnt=0;
	 int errorRcdcnt=0;
	 Map<String, String> errorMap=new HashMap<String, String>();
	 

 /**
  * Invoke.
  */
 public void invoke(){
	 
	 log.debug("ENTERING invoke method");
	 Map<String,List<String>> requestMap  = FileUtils.loadFiles();
	 
	 requestMap.forEach((fileName, requestList) -> {
		 if(requestList.size()>0) {
			 
				for (Iterator<String> iterator = requestList.iterator(); iterator.hasNext();) {
					String request = iterator.next();					 
					log.info("request : "+request);
					postJson(request);
				}
		
			    MailBean mailBean=new MailBean();
		        mailBean.setErrorMap(errorMap);
		        mailBean.setSucessRcdCnt(sucessRcdCnt);
		        mailBean.setErrorRcdcnt(errorRcdcnt);
		        mailBean.setFileName(fileName);
		     
		         try {
		 			MailMethods.sendStatusMail(mailBean);
		 		} catch (MessagingException e) {
		 			e.printStackTrace();
		 		} 
		 }
		});
	 //get the details and save to server
	 if(!requestMap.isEmpty()) {
		 
		 FtpUtils.saveFiletoFtpServer(getUserList());
		/* try {
			 MailMethods.sendMail(PropertyUtils.getUserListMailSubject(), PropertyUtils.getUserListMailBody());
		 } catch (MessagingException e) {
				e.printStackTrace();
			}*/
	 }
 }
 
 
 public void postJson(String json)  {	

	 
	 HttpPut  request = new HttpPut(PropertyUtils.getApiUrl());
	 request.setHeader("Accept", "application/json");
	 request.setHeader("Content-type", "application/json");
	 String result=null;
	 try {
		request.setEntity(new StringEntity(json));
	} catch (UnsupportedEncodingException e1) {
		log.error("not able send the request to API. request : "+json+""+e1.getMessage(),e1);
		
	}
	    CredentialsProvider provider = new BasicCredentialsProvider();
	     provider.setCredentials(
	                AuthScope.ANY,
	                new UsernamePasswordCredentials(PropertyUtils.getApiUser(), PropertyUtils.getApiKey())
	        );
	
	     CloseableHttpClient httpClient = HttpClientBuilder.create()
	             .setDefaultCredentialsProvider(provider).
	             build();
		 CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
		} catch (IOException e) {
			log.error("not able send the request to API"+e.getMessage(),e);
			 sendMail("not able send the request to API", e.getMessage());
		}
		 HttpEntity entity = response.getEntity();
			
	     try {
		 if (entity != null) {
             // return it as a String
              try {
				result = EntityUtils.toString(entity);
			} catch (ParseException|IOException e) {
				log.error("unable to parse the response "+e.getMessage(),e);
				sendMail("unable to parse the response", e.getMessage());
			} 
             log.info("Sucessfully sent");
             ObjectMapper mapper = new ObjectMapper();
             JsonResponse jsonResponse = mapper.readValue(result, JsonResponse.class);
             log.info("got Response : "+result);
             if(jsonResponse.getSuccess().equalsIgnoreCase("false")) {
            	 
            	 ObjectMapper objectMapper = new ObjectMapper();
            	    JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
            	 
            	 errorMap.put(jsonNode.toString(),result);
            	 ++errorRcdcnt; 
             }else if(jsonResponse.getSuccess().equalsIgnoreCase("true")) {
            	 ++sucessRcdCnt;
             }
		 }
	 }catch(IOException e){
		 log.error("failed to parse the response "+e.getMessage(),e); 
         sendMail("recieved unknown Response ","recieved Response  : "+result);
	 }
 }

 
 public String getUserList()  {	

	 
	 HttpGet   request = new HttpGet (PropertyUtils.getUserListApiUrl());
	 request.setHeader("Accept", "application/json");
	 request.setHeader("Content-type", "application/json");
	 String result=null;
	
	    CredentialsProvider provider = new BasicCredentialsProvider();
	     provider.setCredentials(
	                AuthScope.ANY,
	                new UsernamePasswordCredentials(PropertyUtils.getApiUser(), PropertyUtils.getApiKey())
	        );
	
	     CloseableHttpClient httpClient = HttpClientBuilder.create()
	             .setDefaultCredentialsProvider(provider).
	             build();
		 CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
		} catch (IOException e) {
			log.error("not able send the request to API"+e.getMessage(),e);
			 sendMail("not able send the request to API", e.getMessage());
		}

	    	if( response.getStatusLine().getStatusCode()==200) {
	    		HttpEntity entity = response.getEntity();
	    		
	    		
	    		 if (entity != null) {
	    	          // return it as a String
	    	           try {
	    					result = EntityUtils.toString(entity);
	    					
	    				} catch (ParseException|IOException e) {
	    					log.error("unable to parse the response "+e.getMessage(),e);
	    					sendMail("unable to parse the response", e.getMessage());
	    				} 
	    	          log.info("Recieve Suessfully");
	    	       
	    	       //   log.info("got Response : "+result);
	    	        
	    			 }
	    	}
	    	return result;
	 }
 
 
 private void sendMail(String asSubject,String asMessage) {
	 try {
  		MailMethods.sendMail(asSubject, asMessage);
  	} catch (Exception ex) {
  		log.error("Failed to send Mail "+ex.getMessage(),ex);
  		//ex.printStackTrace();
	}	
	 
 }
   
}