package com.thinksns.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.UserDataInvalidException;

public class Follow extends User {
	protected int followId;
	protected int friendId;
	protected String jsonString;

	public int getFriendId() { 
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public int getFollowId() {
		return followId;
	}

	public void setFollowId(int followId) {
		this.followId = followId;
	}
    
	/**
	 * 在aftertimeline中被初始化，参数data为从服务器返回的result
	 * weibo字段包含当前用户最后一条微博。
	 * @param data
	 * @throws DataInvalidException
	 */
	public Follow(JSONObject data) throws DataInvalidException{
		if(data.equals("null")) throw new DataInvalidException();
		try {
			this.initUserInfo(data.getJSONObject("user"));
			if(data.has("id")) this.setFollowId(data.getInt("id"));
			if (data.has("weibo") && !data.getString("weibo").equals("")) {
				this.setLastWeibo(new Weibo(data.getJSONObject("weibo")));
			}
		} catch (JSONException e) {
			throw new UserDataInvalidException();
		}
	}
	
	@Override
	public String toJSON(){
		return jsonString;
	}
	
	
}
