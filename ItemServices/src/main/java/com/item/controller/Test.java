
package com.item.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.item.service.ProcessItems;

public class Test {

	private static Logger LOG = LoggerFactory.getLogger(Test.class);

	
	public static void main(String[] args) {
		
		System.out.println("Available Processors : "+Runtime.getRuntime().availableProcessors());
		
		
		ProcessItems itemWS = new ProcessItems();
 		try {
 			LOG.info("Entering in to the Test");
 			itemWS.invoke();
 			
 		}catch(Exception t) {
 			LOG.error("Exception Occured",t);
 		}
		
	}
}
