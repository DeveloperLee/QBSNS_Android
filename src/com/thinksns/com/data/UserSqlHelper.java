package com.thinksns.com.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.model.User;
import com.thinksns.model.Weibo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UserSqlHelper extends SqlHelper{
	private static UserSqlHelper instance;
	private ThinksnsTableSqlHelper handler;
	
	private UserSqlHelper(Context context){
		this.handler = new ThinksnsTableSqlHelper(context,DB_NAME,null,VERSION);
	}
	
	
	public static UserSqlHelper getInstance(Context context){
		if(instance == null){
			instance = new UserSqlHelper(context);
		}
		
		return instance;
	}
	
	public long addUser(User user,boolean isLogin){
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.id, user.getUid());
		map.put(ThinksnsTableSqlHelper.uname, user.getUserName());
		map.put(ThinksnsTableSqlHelper.token, user.getToken());
		map.put(ThinksnsTableSqlHelper.secretToken, user.getSecretToken());
		map.put(ThinksnsTableSqlHelper.province, user.getProvince());
		map.put(ThinksnsTableSqlHelper.city, user.getCity());
		map.put(ThinksnsTableSqlHelper.location, user.getLocation());
		map.put(ThinksnsTableSqlHelper.face, user.getFace());
		map.put(ThinksnsTableSqlHelper.sex, user.getSex());
		map.put(ThinksnsTableSqlHelper.weiboCount, user.getWeiboCount());
		map.put(ThinksnsTableSqlHelper.followersCount,user.getFollowersCount());
		map.put(ThinksnsTableSqlHelper.followedCount, user.getFollowedCount());
		map.put(ThinksnsTableSqlHelper.isFollowed, user.isFollowed());
		if(!user.isNullForLastWeibo()){
			map.put(ThinksnsTableSqlHelper.lastWeiboId, user.getLastWeibo().getWeiboId());
			map.put(ThinksnsTableSqlHelper.myLastWeibo, user.getLastWeibo().toJSON());
		}
		map.put(ThinksnsTableSqlHelper.userJson, user.toJSON());
		map.put(ThinksnsTableSqlHelper.isLogin, isLogin);
		return handler.getWritableDatabase().insert(ThinksnsTableSqlHelper.tableName, null, map);
	}
	
	public void clear(){
		handler.getWritableDatabase().execSQL("delete from "+ThinksnsTableSqlHelper.tableName);
	}
	
	public User getLoginedUser() throws UserDataInvalidException{
		Cursor result = handler.getReadableDatabase().query(ThinksnsTableSqlHelper.tableName, null, ThinksnsTableSqlHelper.isLogin+"=1", null,null,null,null);
		if(result!=null){//�α겻Ϊ�� 
			User user = new User();
			if(result.moveToFirst()){
				user.setUid(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.id)));
				user.setUserName(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.uname)));
				user.setToken(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.token)));
				user.setSecretToken(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.secretToken)));
				user.setProvince(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.province)));
				user.setCity(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.city)));
				user.setLocation(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.location)));
				user.setFace(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.face)));
				user.setSex(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.sex)));
				user.setWeiboCount(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.weiboCount)));
				user.setFollowedCount(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.followedCount)));
				user.setFollowersCount(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.followersCount)));
				user.setFollowed(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.isFollowed)) == 0);
				int lastWeiboId = result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.lastWeiboId));
				if(lastWeiboId > 0){
					Weibo lastWeibo = new Weibo();
					lastWeibo.setWeiboId(lastWeiboId);
					user.setLastWeibo(lastWeibo);
				}
				
			}else{
				throw new UserDataInvalidException();
			}
			return user;
		}
		throw new UserDataInvalidException();
	}
	
	public User getUser(String sql) throws UserDataInvalidException{
		Cursor result = handler.getReadableDatabase().query(ThinksnsTableSqlHelper.tableName, null, sql, null,null,null,null);
		if(result!=null){//�α겻Ϊ�� 
			User user = new User();
			if(result.moveToFirst()){
				user.setUid(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.id)));
				user.setUserName(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.uname)));
				user.setToken(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.token)));
				user.setSecretToken(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.secretToken)));
				user.setProvince(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.province)));
				user.setCity(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.city)));
				user.setLocation(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.location)));
				user.setFace(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.face)));
				user.setSex(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.sex)));
				user.setWeiboCount(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.weiboCount)));
				user.setFollowedCount(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.followedCount)));
				user.setFollowersCount(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.followersCount)));
				user.setFollowed(result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.isFollowed)) == 0);
				int lastWeiboId = result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.lastWeiboId));
				if(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.myLastWeibo))!= null){
					try {
						user.setLastWeibo(new Weibo(new JSONObject(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.myLastWeibo)))));
					} catch (WeiboDataInvalidException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				user.setUserJson(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.userJson)));
			}else{
				result.close();
				throw new UserDataInvalidException();
			}
			result.close();
			return user;
		}
		throw new UserDataInvalidException();
	}
	@Override
	public void close(){
		handler.close();
	}

	public int updateUser(User user){
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.uname, user.getUserName());
		map.put(ThinksnsTableSqlHelper.token, user.getToken());
		map.put(ThinksnsTableSqlHelper.secretToken, user.getSecretToken());
		map.put(ThinksnsTableSqlHelper.province, user.getProvince());
		map.put(ThinksnsTableSqlHelper.city, user.getCity());
		map.put(ThinksnsTableSqlHelper.location, user.getLocation());
		map.put(ThinksnsTableSqlHelper.face, user.getFace());
		map.put(ThinksnsTableSqlHelper.sex, user.getSex());
		map.put(ThinksnsTableSqlHelper.weiboCount, user.getWeiboCount());
		map.put(ThinksnsTableSqlHelper.followersCount,user.getFollowersCount());
		map.put(ThinksnsTableSqlHelper.followedCount, user.getFollowedCount());
		map.put(ThinksnsTableSqlHelper.isFollowed, user.isFollowed());
		if(!user.isNullForLastWeibo()){
			map.put(ThinksnsTableSqlHelper.lastWeiboId, user.getLastWeibo().getWeiboId());
			map.put(ThinksnsTableSqlHelper.myLastWeibo, user.getLastWeibo().toJSON());
		}
		map.put(ThinksnsTableSqlHelper.userJson, user.toJSON());
		map.put(ThinksnsTableSqlHelper.isLogin, true); 
		int i = handler.getWritableDatabase().update(ThinksnsTableSqlHelper.tableName, map, ThinksnsTableSqlHelper.uid+"="+user.getUid(), null);
		return i;
	}
	
	public int setUserLogout(User user){
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.isLogin, false);
		int i = handler.getWritableDatabase().update(ThinksnsTableSqlHelper.tableName, map, ThinksnsTableSqlHelper.uid+"="+user.getUid(), null);
		return i;
	}
	
	
}
