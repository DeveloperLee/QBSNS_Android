package com.thinksns.com.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.thinksns.model.ListData;
import com.thinksns.model.Message;
import com.thinksns.model.SociaxItem;

public class MyMessageSqlhelper extends SqlHelper{
	private static MyMessageSqlhelper instance;
	private ThinksnsTableSqlHelper weiboTable;
	private ListData<SociaxItem> messageDatas;
	
	private MyMessageSqlhelper(Context context){
		this.weiboTable = new ThinksnsTableSqlHelper(context,DB_NAME,null,VERSION);
	}
	
	public static MyMessageSqlhelper getInstance(Context context){
		if(instance == null){
			instance = new MyMessageSqlhelper(context);
		}
		
		return instance;
	}
	
	private int isread(boolean isread){
		if(isread){
			return 1;
		}else{
			return 0;
		}
	}
	private boolean forRead(int isread){
		if(isread == 1){
			return true;
		}else{
			return false;
		}
	}
	private int isLast(boolean islast){
		if(islast){
			return 1;
		}else{
			return 0;
		}
	}
	private boolean forLast(int islast){
		if(islast == 1){
			return true;
		}else{
			return false;
		}
	}
	private int isOnlyOne(boolean isOnly){
		if(isOnly){
			return 1;
		}else{
			return 0;
		}
	}
	private boolean forOnlyOne(int isOnly){
		if(isOnly==1){
			return true;
		}else{
			return false;
		}
	}
	public long addMessage(Message message){
		ContentValues map = new ContentValues();
		map.put("list_id",message.getListId());
		map.put("member_uid",message.getMemberUid());
		map.put("forNew", message.getForNew());
		map.put("message_num",message.getMessageNum());
		map.put("member_num",message.getMemeberNum());
		map.put("list_ctime",message.getListCtime());
		//map.put("last_message", message.getLastMessage().toString());
		map.put("from_uid", message.getFromUid());
		map.put("toUid",message.getToUid());
		map.put("content", message.getContent());
		map.put("title",message.getTitle());
		map.put("fromUserName", message.getFromUname());
		map.put("fromFace", message.getFromFace());
		map.put("cTime",message.getCtime());
		Log.e("11", "222");
		long l =  weiboTable.getWritableDatabase().insert(ThinksnsTableSqlHelper.myMessageTable, null, map);
		return l;
	}

	
	
	public ListData<SociaxItem> selectMessage(){
		Cursor cursor = weiboTable.getReadableDatabase().query(ThinksnsTableSqlHelper.myMessageTable, null, null, null, null, null, "timestamp DESC");
		messageDatas = new ListData<SociaxItem>();
		cursor.moveToFirst();
		
		if(cursor == null){
			return null;
		}
		while(!cursor.isAfterLast()){
			Message message = new Message();
			message.setListId(cursor.getInt(cursor.getColumnIndex("list_id")));
			message.setFromUid(cursor.getInt(cursor.getColumnIndex("fromUid")));
			message.setContent(cursor.getString(cursor.getColumnIndex("content")));
			message.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			message.setToUid(cursor.getInt(cursor.getColumnIndex("toUid")));
			message.setFromFace(cursor.getString(cursor.getColumnIndex("fromFace")));
			message.setFromUname(cursor.getString(cursor.getColumnIndex("fromUserName")));
			message.setCtime(cursor.getString(cursor.getColumnIndex("cTime")));
	        
			cursor.moveToNext();
			messageDatas.add(message);
			  
		}
		return messageDatas;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		weiboTable.close();
	}
	
	public boolean deleteWeibo(int count){
		if(count >19){
			weiboTable.getWritableDatabase().execSQL("delete from user_message");
		}else if(count >0&& count<20){
			String sql = "delete from user_message where weiboId in (select weiboId from home_weibo order by weiboId limit "+count+")";
			weiboTable.getWritableDatabase().execSQL(sql);
		}
		return false;
	}
}
