package com.thinksns.com.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper; 


/**
 * Android嵌入式SQLite数据库,负责创建Weibo数据缓存表
 * @author Lizihao
 *
 */
public class ThinksnsTableSqlHelper extends SQLiteOpenHelper {
	public ThinksnsTableSqlHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	public static final String tableName = "User";
	public static final String id         = "uid";
	public static final String uname   = "uname";
	public static final String token   = "token";
	public static final String secretToken  = "secretToken";
	public static final String province     = "province";
	public static final String city         = "city";
	public static final String location     = "location";
	public static final String face         = "face";
	public static final String sex			= "sex";
	public static final String weiboCount   = "weiboCount";
	public static final String followersCount = "followersCount";
	public static final String followedCount  = "followedCount";
	public static final String isFollowed     = "isFollowed";
	public static final String lastWeiboId    = "lastWeibo";
	public static final String isLogin        = "login";
	public static final String myLastWeibo    = "myLastWeibo";
	public static final String userJson       = "userJson";
	
	public static final String weiboTable = "home_weibo";
	public static final String weiboId = "weiboId";
	public static final String uid = "uid";
	public static final String userName = "userName";
	public static final String content = "content";
	public static final String cTime = "cTime";
	public static final String from = "weiboFrom";
	public static final String timeStamp = "timestamp";
	public static final String comment = "comment";
	public static final String type = "type";
	public static final String picUrl = "picUrl";
	public static final String thumbMiddleUrl = "thumbMiddleUrl";
	public static final String thumbUrl = "thumUrl";
	public static final String transpond = "transpone";
	public static final String transpondCount = "transpondCount";
	public static final String userface = "userface";
	public static final String transpondId = "transpondId";
	public static final String favorited = "favorited";	
	public static final String weiboJson = "weiboJson";
	
	public static final String atMeTable ="at_me";
	
	public static final String myCommentTable = "my_comment";
	
	public static final String myMessageTable = "user_message";
	
	public static final String siteList ="sites";
	
	public static final String isdel = "isdel";
	
	
	/**
	 * 创建数据库，只在第一次调用时创建，如果数据库修改请删除原
	 * 数据库文件。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "Create Table if not exists "+ tableName +" ( "
					+ id + " integer,"
					+ uname + " varchar not null,"
					+ token + " varchar,"
					+ secretToken + " varchar,"
					+ province + " varchar,"
					+ city + " varchar,"
					+ location + " varchar,"
					+ face + " varchar,"
					+ sex  + " varchar,"
					+ weiboCount +" integer not null,"
					+ followersCount + " integer not null,"
					+ followedCount  + " integer not null,"
					+ isFollowed     + " boolean,"
					+ lastWeiboId    + " integer,"
					+ isLogin +" boolean," 
					+ myLastWeibo+" text,"
					+ userJson+" text)";
		db.execSQL(sql);
	
		//Weibo_database modified at 13-08-08 Lizihao
		//加入了isdel列用来判断是否被管理员删除
		db.execSQL("Create Table if not exists home_weibo (weiboId integer,uid integer," +
				"userName varchar,content text,cTime varchar,weiboFrom integer,isdel integer,timestamp integer," +
				"comment integer,type integer,picUrl text,thumbMiddleUrl text,thumUrl text,transpone text,transpondCount integer," +
				"userface text,transpondId integer,favorited integer,weiboJson text,site_id integer,my_uid integer)");
		
		db.execSQL("Create Table if not exists my_comment (uid integer,timestamp integer,status text,replyUid integer,replyCommentId integer," +
				"weiboId integer,content text,uname text,type text,replyComment text,cTime text,commentId integer,commentUser text,site_id integer,my_uid integer)");
		
		db.execSQL("Create Table if not exists at_me (weiboId integer,uid integer," +
				"userName varchar,content text,cTime varchar,weiboFrom integer,timestamp integer," +
				"comment integer,type integer,picUrl text,thumbMiddleUrl text,thumUrl text,transpone text,transpondCount integer," +
				"userface text,transpondId integer,favorited integer,weiboJson text,site_id integer,my_uid integer)");
		
		db.execSQL("Create Table if not exists user_message(messageId integer,fromUid integer,toUid integer,content text,title text,fromUserName text," +
				"toUserName text,fromFace text,toFace text,isLast integer,isRead integer,timestamp integer,cTime varchar,isOnlyOne integer)");
		
		db.execSQL("Create Table if not exists sites (site_id integer,name text,logo text,uid integer,status_mtime text,url text,description text,email text," +
				"phone text,ctime text,status integer,denied_reason text,isused integer)");	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	//	db.execSQL("ALTER TABLE User ADD COLUMN myLastWeibo text");
	//db.execSQL("ALTER TABLE home_weibo ADD COLUMN weiboJson text");
	//	db.execSQL("ALTER TABLE User ADD COLUMN userJson text");
	}

}
