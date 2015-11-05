package com.thinksns.com.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.Weibo;


/**
 * 数据库操作类，实现Update,Search,insert等功能
 * @author Lizihao 
 *
 */
public class WeiboSqlHelper extends SqlHelper {
	private static WeiboSqlHelper instance;
	private ThinksnsTableSqlHelper weiboTable;
	private ListData<SociaxItem> weiboDatas;
	
	private WeiboSqlHelper(Context context){
		this.weiboTable = new ThinksnsTableSqlHelper(context,DB_NAME,null,VERSION);
	}
	
	public static WeiboSqlHelper getInstance(Context context){
		if(instance == null){
			instance = new WeiboSqlHelper(context);
		}
		
		return instance;
	}
	private int transFrom(String str){
		if(str.equals("WEB")){
			return 0;
		}else if(str.endsWith("ANDROID")){
			return 2;
		}else if(str.equals("IPHONE")){
			return 3;
		}else if(str.equals("PHONE")){
			return 1;
		}
		return 0;
	}
	private int transFavourt(boolean favorited){
		if(favorited){
			return 1;
		}else{
			return 0;
		}
	}
	private boolean isFavourt(int is){
		if(is == 1){
			return true;
		}else{
			return false;
		}
	}
	public long addWeibo(Weibo weibo,int site_id,int myUid){
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.weiboId,weibo.getWeiboId());
		map.put(ThinksnsTableSqlHelper.uid, weibo.getUid());
		map.put(ThinksnsTableSqlHelper.userName,weibo.getUsername());
		map.put(ThinksnsTableSqlHelper.content, weibo.getContent());
		map.put(ThinksnsTableSqlHelper.cTime,weibo.getCtime());
		map.put(ThinksnsTableSqlHelper.from, transFrom(weibo.getFrom().toString()));
		map.put(ThinksnsTableSqlHelper.isdel,weibo.getIsDel());
		map.put(ThinksnsTableSqlHelper.timeStamp,weibo.getTimestamp());
		map.put(ThinksnsTableSqlHelper.comment, weibo.getComment());
		map.put(ThinksnsTableSqlHelper.type,weibo.getType());
		map.put(ThinksnsTableSqlHelper.picUrl, weibo.getPicUrl()!= null?weibo.getPicUrl():"");
		
		map.put(ThinksnsTableSqlHelper.thumbMiddleUrl,weibo.getThumbMiddleUrl()!=null?weibo.getPicUrl():"");
		map.put(ThinksnsTableSqlHelper.thumbUrl, weibo.getThumbUrl()!=null?weibo.getThumbUrl():"");
		if(!weibo.isNullForTranspondId()){
			map.put(ThinksnsTableSqlHelper.transpond,weibo.getTranspond().toJSON());
		}
		//map.put(weiboTable.transpond,weibo.getTranspond().toJSON()!= null?weibo.getTranspond().toJSON():" ");
		map.put(ThinksnsTableSqlHelper.transpondCount, weibo.getTranspondCount());
		map.put(ThinksnsTableSqlHelper.userface,weibo.getUserface());
		map.put(ThinksnsTableSqlHelper.transpondId, weibo.getTranspondId());
		map.put(ThinksnsTableSqlHelper.favorited,transFavourt(weibo.isFavorited()));
		map.put(ThinksnsTableSqlHelper.weiboJson, weibo.toJSON());
		map.put("site_id", site_id);
		map.put("my_uid", myUid);
		long l =  weiboTable.getWritableDatabase().insert(ThinksnsTableSqlHelper.weiboTable, null, map);
		return l;
	}

	
	
	public ListData<SociaxItem> selectWeibo(int site_id,int myUid){
		Cursor cursor = weiboTable.getReadableDatabase().query(ThinksnsTableSqlHelper.weiboTable, null, "site_id="+site_id+" and my_uid="+myUid, null, null, null, ThinksnsTableSqlHelper.weiboId+" DESC");
		weiboDatas = new ListData<SociaxItem>();
		cursor.moveToFirst();
		
		if(cursor == null){
			return null;
		}
		while(!cursor.isAfterLast()){
			Weibo weibo = new Weibo();
			weibo.setWeiboId(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.weiboId)));
			weibo.setUid(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.uid)));
			weibo.setUsername(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.userName)));
			weibo.setContent(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.content)));
			weibo.setCtime(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.cTime)));
			weibo.setFrom(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.from)));
			weibo.setTimestamp(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.timeStamp)));
			weibo.setComment(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.comment)));
			weibo.setType(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.type)));
			weibo.setIsDel(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.isdel)));
			
			weibo.setPicUrl(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.picUrl)));
			weibo.setThumbMiddleUrl(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.thumbMiddleUrl)));
			weibo.setTranspondId(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.transpondId)));
			weibo.setThumbUrl(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.thumbUrl)));
			if(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.transpond))!= null){
				try {
					weibo.setTranspond(new Weibo(new JSONObject(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.transpond)))));
				} catch (WeiboDataInvalidException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
				// TODO Auto-generated catch block
					e.printStackTrace(); 
				}
			}
			weibo.setTranspondCount(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.transpondCount)));
			weibo.setFavorited(isFavourt(cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.favorited))));
			weibo.setUserface(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.userface)));
			weibo.setTempJsonString(cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.weiboJson)));
			cursor.moveToNext();
			weiboDatas.add(weibo);
			
		}
		return weiboDatas;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		weiboTable.close();
	}
	
	public boolean deleteWeibo(int count){
		if(count >19){
			weiboTable.getWritableDatabase().execSQL("delete from home_weibo");
		}else if(count >0&& count<20){
			String sql = "delete from home_weibo where weiboId in (select weiboId from home_weibo order by weiboId limit "+count+")";
			weiboTable.getWritableDatabase().execSQL(sql);
		}
		return false;
	}
	
	public boolean deleteOneWeibo (int weiboId){
		weiboTable.getWritableDatabase().execSQL("delete from home_weibo where weiboId="+weiboId);
		return false;
	}
}
