package com.thinksns.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.net.Request;

public class User extends SociaxItem{
	protected String mUserName;
	protected String mPassword;
	protected String mToken;
	protected String mSecretToken;
	protected int mUid;
	protected String mProvince;
	protected String mCity;
	protected String mLocation;
	protected String mFace;
	protected String mSex;
	protected boolean isInBlackList;
	protected int mWeiboCount;
	protected int mFollowersCount;
	protected int mFollowedCount;
	protected boolean isFollowed;
	protected boolean isVerified;
	protected Weibo  lastWeibo;
	protected String jsonString;
	protected String userJson;
	public String getUserJson() {
		return userJson;
	}

	public void setUserJson(String userJson) {
		this.userJson = userJson;
	}

	public User(){
		super();
	}
	
	public User(JSONObject data) throws DataInvalidException {
		super(data);
		//判断用户最近更新的一条微博
		try {
			this.initUserInfo(data);
			if (data.has("status") && !data.getString("status").equals("")) {
				this.setLastWeibo(new Weibo(data.getJSONObject("status")));
			}

			if (data.has("weibo") && !data.getString("weibo").equals("")) {
				this.setLastWeibo(new Weibo(data.getJSONObject("weibo")));
			}
			
			if(data.has("mini") && !data.getString("mini").equals("null")){
				this.setLastWeibo(new Weibo(data.getJSONObject("mini")));
			}
			//添加判定
			if(data.has("isInBlackList")){
				this.setIsInBlackList(data.getInt("isInBlackList")==1?true:false);
			}
			this.jsonString = data.toString();
		} catch (JSONException e) {
			throw new UserDataInvalidException();
		}
	}
	
	public void initUserInfo(JSONObject data)  throws DataInvalidException{
		try {
			this.setUid(data.getInt("uid"));
			this.setUserName(data.getString("uname"));
			this.setProvince(data.getString("province"));
			this.setCity(data.getString("city"));
			this.setLocation(data.getString("location"));
			this.setFace(data.getString("face"));
			this.setSex(data.getString("sex"));
			this.setWeiboCount(data.getInt("weibo_count"));
			this.setFollowersCount(data.getString("followers_count").equals("false")?0:data.getInt("followers_count"));
			this.setFollowedCount(data.getString("followed_count").equals("false")?0:data.getInt("followed_count"));
			boolean followed = data.getString("is_followed").equals("havefollow") || data.getString("is_followed").equals("eachfollow")?true:false;
			this.setFollowed(followed);
			this.setVerified(data.getInt("is_verified") == 0 ? false : true);
		} catch (JSONException e) {
			throw new UserDataInvalidException();
		}
	}
	
	

	
	public User(int uid,String userName,String password,String token,String secretToken){
		this.setUserName(userName);
		this.setPassword(password);
		this.setToken(token);
		this.setSecretToken(secretToken);
		this.setUid(uid);
	}
	
	public boolean isNullForUid(){
		return this.getUid() == 0;
	}
	
	public String toJSON(){
		return this.jsonString;
	}
	
	public boolean isNullForUserName(){
		String temp = this.getUserName();
		return temp == null || temp.equals(NULL);
	}
	public boolean isNullForProvince(){
		String temp = this.getProvince();
		return temp == null || temp.equals(NULL);
	}
	public boolean isNullForCity(){
		String temp = this.getCity();
		return temp == null || temp.equals(NULL);
	}
	public boolean isNullForLocation(){
		String temp = this.getLocation();
		return temp == null || temp.equals(NULL);
	}
	public boolean isNullForFace(){
		String face = this.getFace();
		return face == null || face.equals(NULL);
	}
	public boolean isNullForSex(){
		String temp = this.getSex();
		return temp == null || temp.equals(NULL);
	}
	public boolean isNullForWeiboCount(){
		return this.getWeiboCount() == 0;
	}
	public boolean isNullForFollowersCount(){
		return this.getFollowersCount() == 0;
	}
	public boolean isNullForFollowedCount(){
		return this.getFollowedCount() == 0;
	}
	
	public boolean isNullForLastWeibo(){
		return this.getLastWeibo() == null || !this.getLastWeibo().checkValid();
	}
	
	public boolean isNullForToken(){
		String temp = this.getToken();
		return temp == null || temp.equals(NULL);
	}
	
	public boolean isNullForSecretToken(){
		String temp = this.getSecretToken();
		return temp == null || temp.equals(NULL);
	}
	
	public boolean hasVerifiedInAndroid(){
		return !(this.isNullForToken() || this.isNullForSecretToken() || this.isNullForUid());
	}
	
	
	@Override
	public boolean checkValid(){
		boolean result = true;
		result = result && !(this.isNullForUid() || this.isNullForUserName() || this.isNullForSex());
		return result;
	}
	
	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public String getToken() {
		return mToken;
	}

	public void setToken(String mToken) {
		Request.setToken(mToken);
		this.mToken = mToken;
	}
	
	public boolean getIsInBlackList() {
		return isInBlackList;
	}

	public void setIsInBlackList(boolean isInBlackList) {
		this.isInBlackList = isInBlackList;
	}

	public String getSecretToken() {
		return mSecretToken;
	}

	public void setSecretToken(String mSecretToken) {
		Request.setSecretToken(mSecretToken);
		this.mSecretToken = mSecretToken;
	}

	public int getUid() {
		return mUid;
	}

	public void setUid(int mUid) {
		this.mUid = mUid;
	}

	public String getProvince() {
		return mProvince;
	}

	public void setProvince(String mProvince) {
		this.mProvince = mProvince;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getFace() {
		return mFace;
	}

	public void setFace(String mFace) {
		this.mFace = mFace;
	}

	public String getSex() {
		return mSex;
	}

	public void setSex(String mSex) {
		this.mSex = mSex;
	}

	public int getWeiboCount() {
		return mWeiboCount;
	}

	public void setWeiboCount(int mWeiboCount) {
		this.mWeiboCount = mWeiboCount;
	}

	public int getFollowersCount() {
		return mFollowersCount;
	}

	public void setFollowersCount(int mFollowersCount) {
		this.mFollowersCount = mFollowersCount;
	}

	public int getFollowedCount() {
		return mFollowedCount;
	}

	public void setFollowedCount(int mFollowedCount) {
		this.mFollowedCount = mFollowedCount;
	}

	public boolean isFollowed() {
		return isFollowed;
	}

	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Weibo getLastWeibo() {
		return lastWeibo;
	}

	public void setLastWeibo(Weibo lastWeibo) {
		this.lastWeibo = lastWeibo;
	}

	@Override
	public String getUserface() {
		return this.getFace();
	}
}
