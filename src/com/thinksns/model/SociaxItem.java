package com.thinksns.model;

import org.json.JSONObject;

import android.graphics.Bitmap;

import com.thinksns.android.Thinksns;
import com.thinksns.exceptions.DataInvalidException;

public abstract class SociaxItem {
	protected static final String NULL = "";
	public SociaxItem(JSONObject jsonData) throws DataInvalidException {
		if(jsonData == null) throw new DataInvalidException();
	}
	
	public SociaxItem() {
		// TODO Auto-generated constructor stub 
	}

	public abstract boolean checkValid();
	public abstract String getUserface();
	
	protected boolean checkNull(String data){
		return data == null || data.equals(NULL);
	}
	
	protected boolean checkNull(int data){
		return data == 0;
	}
	public boolean isNullForHeaderCache(){
		return this.getHeader() == null;
	}
	
	public Bitmap getHeader() {
		return Thinksns.getImageCache().get(this.getUserface());
	}
	public void setHeader(Bitmap header) {
		Thinksns.getImageCache().put(this.getUserface(), header);
	}
	public boolean hasHeader(){
		return !this.getUserface().contains("user_pic_");
	}
	
}
