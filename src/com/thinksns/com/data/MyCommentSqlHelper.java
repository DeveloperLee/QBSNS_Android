package com.thinksns.com.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.model.Comment;
import com.thinksns.model.ListData;
import com.thinksns.model.ReceiveComment;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;
import com.thinksns.model.Weibo; 

public class MyCommentSqlHelper extends SqlHelper{
	private static MyCommentSqlHelper instance;
	private ThinksnsTableSqlHelper weiboTable;
	private ListData<SociaxItem> weiboDatas;
	
	private MyCommentSqlHelper(Context context){
		this.weiboTable = new ThinksnsTableSqlHelper(context,DB_NAME,null,VERSION);
	}
	
	public static MyCommentSqlHelper getInstance(Context context){
		if(instance == null){
			instance = new MyCommentSqlHelper(context);
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
	public long addComment(ReceiveComment comment,int site_id,int myUid){
		ContentValues map = new ContentValues();
		map.put("uid",comment.getUid());
		map.put("timestamp", comment.getTimestemp());
		map.put("weiboId",comment.getWeiboId());
		map.put("commentId", comment.getCommentId());
		if(comment.getStatus()!=null){
			map.put("status",comment.getStatus().toJSON());
		}
		map.put("replyCommentId",comment.getReplyCommentId());
		map.put("replyUid", comment.getReplyUid());
		map.put("content",comment.getContent());
		map.put("uname", comment.getUname());
		map.put("commentUser", comment.getJsonUser());
		map.put("type",comment.getType().toString());
		map.put("replyComment", comment.getReplyJson());
		map.put("cTime", comment.getcTime());
		map.put("site_id", site_id);
		map.put("my_uid", myUid);
		long l =  weiboTable.getWritableDatabase().insert(ThinksnsTableSqlHelper.myCommentTable, null, map);
		return l;
	}

	
	
	public ListData<SociaxItem> selectComment(int site_id,int myUid) {
		Cursor cursor = weiboTable.getReadableDatabase().query(ThinksnsTableSqlHelper.myCommentTable, null, "site_id="+site_id+" and my_uid="+myUid, null, null, null, "timestamp DESC");
		weiboDatas = new ListData<SociaxItem>();
		cursor.moveToFirst();
		
		if(cursor == null){
			return null;
		}
		while(!cursor.isAfterLast()){
			ReceiveComment comment = new ReceiveComment();
			comment.setUid(cursor.getInt(cursor.getColumnIndex("uid")));
			comment.setCommentId(cursor.getInt(cursor.getColumnIndex("commentId")));
			comment.setContent(cursor.getString(cursor.getColumnIndex("content")));
			comment.setcTime(cursor.getString(cursor.getColumnIndex("cTime")));
			comment.setUname(cursor.getString(cursor.getColumnIndex("uname")));
			if(cursor.getString(cursor.getColumnIndex("replyComment"))!= null){
				try {
					try {
						comment.setReplyComment(new Comment(new JSONObject(cursor.getString(cursor.getColumnIndex("replyComment")))));
					} catch (DataInvalidException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (JSONException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			comment.setTimestemp(cursor.getInt(cursor.getColumnIndex("timestamp")));
			comment.setReplyCommentId(cursor.getInt(cursor.getColumnIndex("replyCommentId")));
			comment.setReplyUid(cursor.getInt(cursor.getColumnIndex("replyUid")));
			if(cursor.getString(cursor.getColumnIndex("status"))!= null){
				try {
					comment.setStatus(new Weibo(new JSONObject(cursor.getString(cursor.getColumnIndex("status")))));
				} catch (WeiboDataInvalidException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(cursor.getString(cursor.getColumnIndex("commentUser"))!= null){
					try {
						comment.setUser(new User(new JSONObject(cursor.getString(cursor.getColumnIndex("commentUser")))));
					} catch (DataInvalidException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			comment.setWeiboId(cursor.getInt(cursor.getColumnIndex("weiboId")));
			comment.setType(cursor.getString(cursor.getColumnIndex("type")));
		
			cursor.moveToNext();
			weiboDatas.add(comment);
			
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
}
