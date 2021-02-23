package com.item.bean;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class MailBean {

	Map<String, String> errorMap;
	int sucessRcdCnt;
	 int errorRcdcnt;
	 String fileName;
	
	
}
