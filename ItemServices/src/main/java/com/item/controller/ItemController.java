package com.item.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.item.service.ProcessItems;
import com.item.utils.PropertyUtils;

/**
 * The Class ItemController.
 */
public class ItemController {

	private static Logger LOG = LoggerFactory.getLogger(ItemController.class);
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		ScheduledFuture<?> handle =
		        scheduler.scheduleWithFixedDelay(new Runnable() {
		             public void run() { 
		            	 
		            	 long startTime = System.nanoTime();
		         		ProcessItems itemWS = new ProcessItems();
		         		try {
		         			LOG.info("Entering in to the ItemController");
		         			itemWS.invoke();
		         			
		         		}catch(Exception t) {
		         			LOG.error("Exception Occured",t);
		         		}finally {
		         			long endTime   = System.nanoTime();
			        		 long totalTime = endTime - startTime;
			        		 long convert = TimeUnit.SECONDS.convert(totalTime, TimeUnit.NANOSECONDS);
			        		 LOG.info("Total Time taken : "+ convert+" Seconds");
		         		}
		         		
		        	
		            	 
		             }
		        }, 1, PropertyUtils.getFrequency(), TimeUnit.SECONDS);
		
		LOG.info(""+PropertyUtils.getFrequency());
	}
	
}
