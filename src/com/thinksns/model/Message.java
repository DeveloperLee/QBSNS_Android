package com.thinksns.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsMyWeibo;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.MessageDataInvalidException;
import com.thinksns.unit.PlainTextDecoder;

 
public class Message extends SociaxItem {
	private int listId;
	private int memberUid;
	private int forNew;
	private int messageNum;
	private int toUid;
	private String ctime;
	private int listCtime;
	private int fromUid;
	private int type;
	private String title;
	private int memeberNum;
	private String minMax;
	private int mtime;
	private String content;
	private Message LastMessage;
	private String fromUname;
	private String fromFace;
	private int   timeStmap;
	private int meesageId;
	
	public int getMeesageId() {
		return meesageId;
	}


	public void setMeesageId(int meesageId) {
		this.meesageId = meesageId;
	}


	public Message getLastMessage() {
		return LastMessage;
	}


	public void setLastMessage(Message lastMessage) {
		LastMessage = lastMessage;
	}

	
	public Message(){}
	public Message(JSONObject data,boolean type) throws DataInvalidException{
		super(data);
		try {
			if(type){
				this.setFromUid(data.getInt("from_uid"));
				this.setContent(data.getString("content"));				
			}else{
				this.setListId(data.getInt("list_id"));
				this.setFromUid(data.getInt("from_uid"));
				this.setMeesageId(data.getInt("message_id"));
				this.setType(data.getInt("type"));
				String content = extractMessageContent(data, this.getType());
				this.setContent(content);
				this.setMtime(data.getInt("mtime"));
				this.setFromFace(data.getString("from_face"));
				this.setFromUname(data.getString("from_uname"));
				this.setTimeStmap(data.getInt("timestmap"));
				this.setCtime(data.getString("ctime"));
			}

	
		} catch (JSONException e) {
			throw new MessageDataInvalidException();
		}
	}
		
	public Message(JSONObject data) throws DataInvalidException{
		super(data);
		try {
			this.setListId(data.getInt("list_id"));
			this.setMemberUid(data.getInt("member_uid"));
			this.setForNew(data.getInt("new"));
			this.setMessageNum(data.getInt("message_num"));
			this.setCtime(data.getString("ctime"));
			this.setListCtime(data.getInt("list_ctime"));
			this.setFromUid(data.getInt("from_uid"));
			this.setType(data.getInt("type"));
			this.setTitle(data.getString("title"));
			this.setMemeberNum(data.getInt("member_num"));
			this.setMinMax(data.getString("min_max").equals("")?"":data.getString("min_max"));
			this.setMtime(data.getInt("mtime"));
			//this.setContent(data.getString("content"));
			String content = extractMessageContent(data,this.getType());
			this.setContent(content);
			this.setFromUname(data.getString("from_uname"));
			this.setToUid(data.has("to_uid")?data.getInt("to_uid"):0);
			this.setFromFace(data.getString("from_face"));
			if(data.getString("last_message")!=""){
				this.setLastMessage(new Message(data.getJSONObject("last_message"),true));
			}
		} catch (JSONException e) {
			throw new MessageDataInvalidException();
		}
	}

    //解析json
	private String extractMessageContent(JSONObject data,int type) throws JSONException {
		
		String JSONContent = PlainTextDecoder.decode(data.getString("content"));
		
	    boolean user_matched = (Thinksns.getMy().getUid() == data.getInt("from_uid"));
	    String fixed_content = "";
		switch(type){
		    case 1:
		    	   fixed_content = JSONContent;
		           break;
		    case 2:
		    	   JSONObject contentObject = new JSONObject(JSONContent);
		    	   if(user_matched){
		    		   fixed_content = contentObject.getString("content1");
		    	   }else{
		    		   fixed_content = contentObject.getString("content2");
		    	   }
		    	   break;
		    case 3:
		    	   contentObject = new JSONObject(JSONContent);
		    	   fixed_content = contentObject.getString("content");
		    	   break;
		}
		
		return fixed_content;
	}

	public int getToUid() {
		return toUid;
	}


	public void setToUid(int toUid) {
		this.toUid = toUid;
	}


	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public int getMemberUid() {
		return memberUid;
	}

	public void setMemberUid(int memberUid) {
		this.memberUid = memberUid;
	}

	public int getForNew() {
		return forNew;
	}

	public void setForNew(int forNew) {
		this.forNew = forNew;
	}

	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}



	public int getListCtime() {
		return listCtime;
	}

	public void setListCtime(int listCtime) {
		this.listCtime = listCtime;
	}

	public int getFromUid() {
		return fromUid;
	}

	public void setFromUid(int fromUid) {
		this.fromUid = fromUid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getMemeberNum() {
		return memeberNum;
	}

	public void setMemeberNum(int memeberNum) {
		this.memeberNum = memeberNum;
	}

	public String getMinMax() {
		return minMax;
	}

	public void setMinMax(String minMax) {
		this.minMax = minMax;
	}

	public int getMtime() {
		return mtime;
	}

	public void setMtime(int mtime) {
		this.mtime = mtime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromUname() {
		return fromUname;
	}

	public void setFromUname(String fromUname) {
		this.fromUname = fromUname;
	}

	public String getFromFace() {
		return fromFace;
	}

	public void setFromFace(String fromFace) {
		this.fromFace = fromFace;
	}

	public int getTimeStmap() {
		return timeStmap;
	}

	public void setTimeStmap(int timeStmap) {
		this.timeStmap = timeStmap;
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isNullForMinMax(){
		return this.minMax.equals(null)||this.minMax.equals("");
	}

}
