package com.thinksns.model;

import java.util.ArrayList;


public class ListData<T extends SociaxItem> extends ArrayList<SociaxItem> {
	public static enum Position{
		BEGINING,END
	}
	public static enum DataType{
		COMMENT,WEIBO,USER,RECEIVE,FOLLOW,SEARCH_USER 
	}
	
}
