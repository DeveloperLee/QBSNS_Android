package com.thinksns.com.data;

public abstract class SqlHelper {
	protected static final int VERSION = 12;
	protected static final String DB_NAME = "thinksns";
	
	public abstract void close();
	
	public int tranBoolean(boolean value){
		if(value){
			return 1;
		}else{
			return 0; 
		}
	}
}
