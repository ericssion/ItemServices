
package com.item.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.item.request.JsonResponse;
import com.item.request.Request;
import com.item.utils.FileUtils;
import com.item.utils.MailMethods;
import com.item.utils.PropertyUtils;

/**
 * The Class ProcessPatient.
 */
public class ProcessItems {

	private static Logger log = LoggerFactory.getLogger(ProcessItems.class);
	

 /**
  * Invoke.
  */
 public void invoke(){
	 
	 log.debug("ENTERING invoke method");
	List <Request> requestList  = FileUtils.loadFiles();
	 
	/*
	 * requestList.parallelStream().forEach(s->{
	 * 
	 * ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	 * String json=null; try { json = ow.writeValueAsString(s);
	 * log.info("request : "+json); } catch (JsonProcessingException e) {
	 * log.error("unable to convert  the request to String :  "+e.getMessage(),e); }
	 * postJson(s);
	 * 
	 * });
	 */
	
	
	
	if(requestList.size()>0) {
		 
		for (Iterator<Request> iterator = requestList.iterator(); iterator.hasNext();) {
			Request request = (Request) iterator.next();
			 ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			 String json=null;
				try {
					json = ow.writeValueAsString(request);
					log.info("request : "+json);
				} catch (JsonProcessingException e) {
					log.error("unable to convert  the request to String :  "+e.getMessage(),e);
				}
			
			postJson(request);
		}
	 }
 }
 
 
 public void postJson(Request req)  {	

	 
	 HttpPut  request = new HttpPut(PropertyUtils.getApiUrl());
	 request.setHeader("Accept", "application/json");
	 request.setHeader("Content-type", "application/json");
	 String result=null;
	 ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	 String json=null;
	try {
		json = ow.writeValueAsString(req);
	} catch (JsonProcessingException e1) {
		// TODO Auto-generated catch block
		log.error("not able send the request to API"+e1.getMessage(),e1);
	}
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
            	 sendMail("Request failed ","Sent Request : "+json+"Response recieved : "+jsonResponse.getMessage());
             }   
         }
	 }catch(IOException e){
		 log.error("failed to parse the response "+e.getMessage(),e); 
         sendMail("recieved unknown Response ","recieved Response  : "+result);
	 }
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