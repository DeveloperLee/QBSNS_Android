package com.thinksns.android;

import java.util.ArrayList;
import java.util.Stack;
import java.util.WeakHashMap;
import com.thinksns.api.Api;
import com.thinksns.com.data.AtMeSqlHelper;
import com.thinksns.com.data.MyCommentSqlHelper;
import com.thinksns.com.data.MyMessageSqlhelper;
import com.thinksns.com.data.SitesSqlHelper;
import com.thinksns.com.data.SqlHelper;
import com.thinksns.com.data.UserSqlHelper;
import com.thinksns.com.data.WeiboSqlHelper;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.model.ApproveSite;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;
import com.thinksns.net.Request;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;

public class Thinksns extends Application {
	private Api api;
	private Api.Friendships friendships;
	private Api.Statuses    statuses;
	private Api.Oauth       oauth;
	private Api.Favorites   favorites;
	private Api.Message     messages;
	private Api.Users       users;
	private Api.Sites 	    sites;
	private static ApproveSite mySite;
	private static String url;
	private static int delIndex;
	
	public static int getDelIndex() {
		return delIndex;
	}

    
	public static void setDelIndex(int delIndex) {
		Thinksns.delIndex = delIndex;
	}



	public static ApproveSite getMySite() {
		return mySite;
	}



	public static void setMySite(ApproveSite mySite) {
		Thinksns.mySite = mySite;
	}

	

	public static final String NULL = "";
	private static User my;
	private Stack<SqlHelper> sqlHelper;
	private static WeakHashMap<String,Bitmap> imageCache;
	private static ListData<SociaxItem> lastWeiboList;
	
	public Thinksns() {
		super();
		sqlHelper = new Stack<SqlHelper>();
		imageCache = new WeakHashMap<String,Bitmap>();
		
	}
	


	public Api.Status initOauth() throws ApiException{
		return this.getOauth().requestEncrypKey();
	}
	
/*	public void initStack(){
		setActivityStack(new ActivityStack());
	}*/
	
	
	public void startActivity(Activity now,Class<? extends Activity> target,Bundle data) {
		Intent intent = new Intent ();
		intent.setClass(now, target);
		if(data != null){
			if(intent.getExtras() != null){
				intent.replaceExtras(data);
			}else{
				intent.putExtras(data);
			}
		}
		now.startActivity(intent);
	}
	
	public void startActivityForResult(Activity now,Class<? extends Activity> target,Bundle data){
		Intent intent = new Intent ();
		intent.setClass(now, target);
		if(data != null){
			if(intent.getExtras() != null){
				intent.replaceExtras(data);
			}else{
				intent.putExtras(data);
			}
		}
		now.startActivityForResult(intent, 3456);
	}
	public void initApi(){
		SitesSqlHelper db = this.getSiteSql();
		ApproveSite as = null;
		try{
			as  = db.getInUsed();
		}catch(Exception e){
			this.api = Api.getInstance(getApplicationContext(),false,null);
			Thinksns.setMySite(as);
			return ;
		}
		if(as==null){
			this.api = Api.getInstance(getApplicationContext(),false,null);
		}else{
			this.api =Api.getInstance(getApplicationContext(), true, dealUrl(as.getUrl()));
		}
		Thinksns.setMySite(as);
	}
	
	public Api getApi(){
		return this.api;
	}
	
	public static WeakHashMap<String,Bitmap> getImageCache(){
		return imageCache;
	}
	
	public boolean HasLoginUser(){
		UserSqlHelper db = this.getUserSql();
		try {
			User user = db.getLoginedUser();
			Request.setToken(user.getToken());
			Request.setSecretToken(user.getSecretToken());
			Thinksns.setMy(user);
			return true;
		} catch (UserDataInvalidException e) {
			return false;
		}
	}
	
	public UserSqlHelper getUserSql(){
		UserSqlHelper db = UserSqlHelper.getInstance(getApplicationContext());
		sqlHelper.add(db);
		return db;
	}
	
	public WeiboSqlHelper getWeiboSql(){
		WeiboSqlHelper db = WeiboSqlHelper.getInstance(getApplicationContext());
		sqlHelper.add(db);
		return db;		
	}
	
	public AtMeSqlHelper getAtMeSql(){
		AtMeSqlHelper db = AtMeSqlHelper.getInstance(getApplicationContext());
		sqlHelper.add(db);
		return db;	
	}
	
	public MyMessageSqlhelper getMyMessageSql(){
		MyMessageSqlhelper db = MyMessageSqlhelper.getInstance(getApplicationContext());
		sqlHelper.add(db);
		return db;	
	}
	public MyCommentSqlHelper getMyCommentSql(){
		MyCommentSqlHelper db = MyCommentSqlHelper.getInstance(getApplicationContext());
		sqlHelper.add(db);
		return db;	
	}
	
	public SitesSqlHelper getSiteSql(){
		SitesSqlHelper db = SitesSqlHelper.getInstance(getApplicationContext());
		sqlHelper.add(db);
		return db;
	}
	public void closeDb(){
		if(!sqlHelper.empty()){
			for(SqlHelper db:sqlHelper){
				db.close();
			}
		}
	}
	
	public Api.Friendships getFriendships() {
		if(friendships == null){
			friendships = new Api.Friendships();
		}
		return friendships;
	}
	
	public Api.Statuses getStatuses() {
		if(statuses == null){
			statuses = new Api.Statuses();
		}
		return statuses;
	}
	
	public Api.Oauth getOauth() {
		if(oauth == null){
			oauth = new Api.Oauth();
		}
		return oauth;
	}

	public Api.Favorites getFavorites() {
		if(favorites == null){
			favorites = new Api.Favorites();
		}
		return favorites;
	}

	public Api.Message getMessages() {
		if(messages == null){
			messages = new Api.Message();
		}
		return messages;
	}
	
	public Api.Users getUsers() {
		if(users == null){
			users = new Api.Users();
		}
		return users;
	}
	
	public Api.Sites getSites(){
		if(sites == null){
			sites = new Api.Sites();
		}
		return sites;
	}

/*	public ActivityStack getActivityStack() {
		return activityStack;
	}

	public void setActivityStack(ActivityStack activityStack) {
		this.activityStack = activityStack;
	}*/

	public static User getMy() {
		return my;
	}

	public static void setMy(User my) {
		Thinksns.my = my;
	}



	public static ListData<SociaxItem> getLastWeiboList() {
		return lastWeiboList;
	}



	public static void setLastWeiboList(ListData<SociaxItem> lastWeiboList) {
		Thinksns.lastWeiboList = lastWeiboList;
	}
	
	public static String[] dealUrl(String url){
		String[] tempUrl = new String[3];
		String temp ="";
		String[] buttonText = url.substring(7).split("/");
		if(buttonText.length == 1){
			tempUrl[0] = buttonText[0];
			if(tempUrl[0].contains(":")){
				tempUrl[0] = buttonText[0].split(":")[0];
				tempUrl[2] = buttonText[0].split(":")[1];
			}else{
				tempUrl[2]=null;
			}
			tempUrl[1] = "";
		}else{
			tempUrl[0] = buttonText[0];
			if(tempUrl[0].contains(":")){
				tempUrl[0] = buttonText[0].split(":")[0];
				tempUrl[2] = buttonText[0].split(":")[1];
			}else{
				tempUrl[2]=null;
			}
			for(int i=1;i<buttonText.length;i++){
				temp += buttonText[i]+"/";
			}
			tempUrl[1] ="/"+ temp;
		}
		return tempUrl;
	}
	
	//判断网络状态
	public boolean isNetWorkOn(){
		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cwjManager.getActiveNetworkInfo()!=null){
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		return netSataus;
	}
	
	public static ArrayList<Activity> allActivity=new ArrayList<Activity>();//保存Activity
	public static int lastActivityId;
	
	// 通过name获取Activity对象
	public static Activity getActivityByName(String name) {
		Activity getac = null;
			for (Activity ac : allActivity) {
				if (ac.getClass().getName().indexOf(name) >= 0) {
					getac = ac;
				}
			}
		return getac;
	}

	// 结束
	public static void exitApp(){
		if(null != (ThinksnsHome)Thinksns.getActivityByName("ThinksnsHome"))
			((ThinksnsHome)Thinksns.getActivityByName("ThinksnsHome")).exitApp();
		return;	
	}
	
}


