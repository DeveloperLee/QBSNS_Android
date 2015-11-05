package com.thinksns.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.UserDataInvalidException;

public class SearchUser extends User { 


	public SearchUser(JSONObject data) throws DataInvalidException{
		if(data.equals("null")) throw new DataInvalidException();
		try {
			this.initUserInfo(data);
			if (data.has("mini") && !data.getString("mini").equals("null")) {
				this.setLastWeibo(new Weibo(data.getJSONObject("mini"),"SEARCH_USER"));
			}
		} catch (JSONException e) {
			throw new UserDataInvalidException();
		}
	}
	
	@Override
	public void initUserInfo(JSONObject data) throws DataInvalidException {
		try {
			this.setUid(data.getInt("uid"));
			this.setUserName(data.getString("uname"));
			this.setLocation(data.getString("location"));
			this.setFace(data.getString("face"));
			this.setSex(data.getString("sex"));
			this.setFollowersCount(data.getInt("followers_count"));
			this.setFollowedCount(data.getInt("followed_count"));
			boolean followed = data.getString("is_followed").equals("havefollow") || data.getString("is_followed").equals("eachfollow")?true:false;
			this.setFollowed(followed);
			this.setVerified(data.getInt("is_init") == 0 ? false : true);
		} catch (JSONException e) {
			throw new UserDataInvalidException();
		}
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return super.checkValid();
	}
	
	
	
}
