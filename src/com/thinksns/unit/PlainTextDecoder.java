package com.thinksns.unit;

public class PlainTextDecoder {
	
	public static String decode(String text){
		
		
		text=text.replace("&amp;","&"); 
		text=text.replace("&nbsp;"," "); 
		text=text.replace("&lt;","<"); 
		text=text.replace("&gt;",">"); 
		text=text.replace("&quot;","\"");  
		//decodedTextString=text.replace("&nbsp;&nbsp;&nbsp;&nbsp;"," "); 
		
		return text;
		
	}

}
