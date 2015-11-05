package com.thinksns.model;

import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import com.thinksns.android.Thinksns;
import com.thinksns.exceptions.WeiboDataInvalidException;

public class Weibo extends SociaxItem{
	public static final String TAG = "Weibo";
	
	public static enum From{
		WEB,WAP,ANDROID,IPHONE
	}
//	public static enum From{
//		网站,手机网页,Android客户端,iPhone客户端,
//	}
	private int weiboId;
	private int uid;
	private String content;
	private String cTime;
	private From from;
	private int timestamp;
	private int comment;
	private int type;
	private String picUrl;
	private String thumbMiddleUrl;
	private String thumbUrl;
	private Weibo transpond;
	private int transpondCount;
	private String userface;
	private int transpondId;
	private boolean favorited;
	private String username;
	private String jsonString;
	private String tempJsonString;
	private int is_del;
	

	public String getTempJsonString() {
		return tempJsonString;
	}
	public void setTempJsonString(String tempJsonString) {
		this.tempJsonString = tempJsonString;
	}

	public static final int MAX_CONTENT_LENGTH = 140;


	
	public Weibo(){
		
	}
	
	
	public boolean isInvalidWeibo(){
		return !"".equals(this.content);
	}
	
	public Weibo(JSONObject weiboData,String str) throws WeiboDataInvalidException{
		try {
			this.setComment(weiboData.getInt("comment"));
			this.setContent(weiboData.getString("content"));
			this.setCtime(weiboData.getString("ctime"));
			this.setWeiboId(weiboData.getInt("weibo_id"));
			this.setUid(weiboData.getInt("uid"));
			this.setFrom(weiboData.getInt("from"));
			this.setType(weiboData.getInt("type"));
			this.setTranspondCount(weiboData.getInt("transpond"));
			this.setTranspondId(weiboData.getInt("transpond_id"));
			this.setIsDel(weiboData.getInt("isdel"));
		
			if(this.hasImage()){
				this.setPicUrl(weiboData.getJSONObject("type_data").getString("picurl"));
				this.setThumbMiddleUrl(weiboData.getJSONObject("type_data").getString("thumbmiddleurl"));
				this.setThumbUrl(weiboData.getJSONObject("type_data").getString("thumburl"));
			}
			
			if(!this.isNullForTranspondId()){
				Weibo transpond = new Weibo(weiboData.getJSONObject("transpond_data"));
				this.setTranspond(transpond);
			}
			this.jsonString = weiboData.toString();
		} catch (JSONException e) {
			throw new WeiboDataInvalidException(e.getMessage());
		}		
	}
	public Weibo(JSONObject weiboData) throws WeiboDataInvalidException{
		try {
			this.setComment(weiboData.getInt("comment"));
			this.setContent(weiboData.getString("content"));
			this.setCtime(weiboData.getString("ctime"));
			this.setWeiboId(weiboData.getInt("weibo_id"));
			this.setUid(weiboData.getInt("uid"));
			this.setTimestamp(weiboData.getInt("timestamp"));
			this.setFrom(weiboData.getInt("from"));
			this.setType(weiboData.getInt("type"));
			this.setTranspondCount(weiboData.getInt("transpond"));
			this.setTranspondId(weiboData.getInt("transpond_id"));
			this.setIsDel(weiboData.getInt("isdel"));
			if(isDelByAdmin()){
				this.setContent("对不起，该微博已被管理员删除。");
			}
			if(weiboData.getInt("favorited") != 0){
				this.setFavorited(true);
			}else{
				this.setFavorited(false);
			}
			this.setUserface(weiboData.getString("face"));
			this.setUsername(weiboData.getString("uname"));
			
			//当这个微博是图片微博并且没有被管理员删除时，填充图片
			if(this.hasImage()){
				this.setPicUrl(weiboData.getJSONObject("type_data").getString("picurl"));
				this.setThumbMiddleUrl(weiboData.getJSONObject("type_data").getString("thumbmiddleurl"));
				this.setThumbUrl(weiboData.getJSONObject("type_data").getString("thumburl"));
			}
			
			if(!this.isNullForTranspondId()){
				Weibo transpond = new Weibo(weiboData.getJSONObject("transpond_data"));
				this.setTranspond(transpond);
			}
			this.jsonString = weiboData.toString();
		} catch (JSONException e) {
			throw new WeiboDataInvalidException(e.getMessage());
		}
		
	}
	public String toJSON(){
		return this.jsonString;
	}
	
	public boolean checkContent(){
		return this.content.length() <= MAX_CONTENT_LENGTH;
	}
	
	public boolean checkContent(String content){
		return content.length() <= MAX_CONTENT_LENGTH;
	}
	
	public boolean isNullForComment(){
		return this.comment == 0;
	}
	
	public boolean isNullForContent(){
		return this.content.equals(NULL) || this.content == null;
	}
	
	public boolean isNullForCtime(){
		return this.cTime.equals(NULL) || this.cTime == null;
	}
	
	public boolean isNullForWeiboId(){
		return this.weiboId == 0;
	}
	
	public boolean isNullForUid(){
		return this.uid == 0;
	}
	
	public boolean isNullForTimestamp(){
		return this.timestamp ==0;
	}
	
	
	public boolean isNullForType(){
		return this.type == 0;
	}
	
	public boolean isNullForTranspond(){
		if(!this.isNullForTranspondId())
			return this.transpond == null;
		return true;
	}
	
	public boolean isNullForTranspondId(){
		return this.transpondId == 0;
	}
	
	public boolean isNullForTranspondCount(){
		return this.transpondCount == 0;
	}
	
	
	public boolean isNullForUserFace(){
		return this.userface == null || this.userface.equals(NULL);
	}
	
	public boolean isNullForUserName(){
		return this.username == null || this.username.equals(NULL);
	}
	
	public boolean isNullForThumbCache(){
		return this.getThumb() == null;
	}
	
	public boolean isNullForThumbMiddleCache(){
		return this.getThumbMiddle() == null;
	}
	public boolean isNullForThumbLargeCache(){
		return this.getPicUrl() == null;
	}

	
	public boolean isNullForPic(){
		if(this.hasImage())
			return this.picUrl.equals(NULL);
		return true;
	}
	
	
	
	public boolean isNullForThumbMiddleUrl(){
		if(this.hasImage())
			return this.thumbMiddleUrl.equals(NULL);
		return true;
	}
	
	public boolean isNullForThumbUrl(){
		if(this.hasImage())
			return this.thumbUrl.equals(NULL);
		return true;
	}
	
	public boolean hasImage(){
		return this.type == 1 && this.isNullForTranspondId();
	}
	
	@Override
	public boolean checkValid(){
		boolean result = true;
		if(this.hasImage()){
			result = result && !(this.isNullForThumbMiddleUrl() || this.isNullForThumbUrl() || this.isNullForPic());
		}
		
		if(!this.isNullForTranspondId()){
			result = result && !this.isNullForTranspond();
		}
		
		result = result && !(this.isNullForContent() || this.isNullForCtime() || this.isNullForUid() || this.isNullForTimestamp() || this.isNullForUserFace() || this.isNullForWeiboId() || this.isNullForUserName());	
		return result;
	}
	

	
	

  
	public int getWeiboId() {
		return weiboId;
	}


	public void setWeiboId(int weiboId) {
		this.weiboId = weiboId;
	}


	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getCtime() {
		return cTime;
	}


	public void setCtime(String cTime) {
		this.cTime = cTime;
	}


	public From getFrom() {
		return from;
	}


	public void setFrom(int from) {
		switch(from){
		case 0:
			this.from = From.WEB;
			break;
		case 1:
			this.from = From.WAP;
			break;
		case 2:
			this.from = From.ANDROID;
			break;
		case 3:
			this.from = From.IPHONE;
			break;
		}
	}
	
	/*public void setFrom(int from) {
		switch(from){
		case 0:
			this.from = From.网站;
			break;
		case 1:
			this.from = From.手机网页;
			break;
		case 2:
			this.from = From.Android客户端;
		case 3:
			this.from = From.iPhone客户端;
		}
	}*/

    
	public int getComment() {
		return comment;
	}


	public void setComment(int comment) {
		this.comment = comment;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getPicUrl() {
		return picUrl;
	}


	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}


	public String getThumbMiddleUrl() {
		return thumbMiddleUrl;
	}


	public void setThumbMiddleUrl(String thumbMiddleUrl) {
		this.thumbMiddleUrl = thumbMiddleUrl;
	}


	public String getThumbUrl() {
		return thumbUrl;
	}


	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
    
	/**
	 * 判断是否被系统管理员删除
	 * Modified by lizihao 13-08-07
	 * @return
	 */
    public boolean isDelByAdmin(){
    	return this.is_del == 2;
    }
	
	public int getIsDel(){
		return this.is_del;
	}
	
	public void setIsDel(int flag){
		this.is_del = flag;
	}
	@Override
	public String getUserface() {
		return userface;
	}


	public void setUserface(String userface) {
		this.userface = userface;
	}


	public Weibo getTranspond() {
		return transpond;
	}


	public void setTranspond(Weibo transpond) {
		this.transpond = transpond;
	}


	public int getTranspondCount() {
		return transpondCount;
	}


	public void setTranspondCount(int transpondCount) {
		this.transpondCount = transpondCount;
	}


	public int getTranspondId() {
		return transpondId;
	}


	public void setTranspondId(int transpondId) {
		this.transpondId = transpondId;
	}


	public int getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}


	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}
	public Bitmap getThumb() {
		return Thinksns.getImageCache().get(this.getThumbUrl());
	}
	public void setThumb(Bitmap thumb) {
		Thinksns.getImageCache().put(this.getThumbUrl(), thumb);
	}

	public Bitmap getThumbMiddle() {
		return Thinksns.getImageCache().get(this.getThumbMiddleUrl());
	}
	public void setThumbMiddle(Bitmap thumbMiddle) {
		Thinksns.getImageCache().put(this.getThumbMiddleUrl(), thumbMiddle);
	}
	
	public Bitmap getThumbLarge(){
		return Thinksns.getImageCache().get(this.getPicUrl());
	}
	
	public void setThumbLarge(Bitmap thumbLarge){
		Thinksns.getImageCache().put(this.getPicUrl(), thumbLarge);
	}
	
	

}
