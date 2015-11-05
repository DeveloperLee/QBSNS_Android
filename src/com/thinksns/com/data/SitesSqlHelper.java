package com.thinksns.com.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.thinksns.model.ApproveSite;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;

public class SitesSqlHelper extends SqlHelper{
	private static SitesSqlHelper instance;
	private ThinksnsTableSqlHelper sitesTable;
	private ListData<SociaxItem> sitesDatas;
	
	private SitesSqlHelper(Context context){
		this.sitesTable = new ThinksnsTableSqlHelper(context,DB_NAME,null,VERSION);
	}
	
	public static SitesSqlHelper getInstance(Context context){
		if(instance == null){
			instance = new SitesSqlHelper(context);
		}
		
		return instance;
	}

	public long addSites(ApproveSite site){
		ContentValues map = new ContentValues(); 
		map.put("site_id",site.getSite_id());
		map.put("name", site.getName());
		map.put("url", site.getUrl());
		map.put("logo", site.getLogo());
		map.put("ctime", site.getCtime());
		map.put("status", tranBoolean(site.isStatus()));
		map.put("denied_reason", site.getDenied_reason());
		map.put("status_mtime", site.getStatus_mtime());
		map.put("description", site.getDescription());
		map.put("email", site.getEmail());
		map.put("phone", site.getPhone());
		map.put("uid", site.getUid());
		map.put("isused",1);
		long l =  sitesTable.getWritableDatabase().insert(ThinksnsTableSqlHelper.siteList, null, map);
		return l;
	}
	
	//锟斤拷锟斤拷某锟斤拷某锟斤拷锟斤拷站为锟斤拷前使锟斤拷
	public void setSiteForUsed(ApproveSite site){
		sitesTable.getWritableDatabase().execSQL("update "+ThinksnsTableSqlHelper.siteList+" set isused=0 where isused=1");
		sitesTable.getWritableDatabase().execSQL("update "+ThinksnsTableSqlHelper.siteList+" set isused=1 where site_id="+site.getSite_id());
	}
	
	//锟斤拷取站锟斤拷
	public ListData<SociaxItem> getSitesList(String sql){
		Cursor cursor = sitesTable.getReadableDatabase().query(ThinksnsTableSqlHelper.siteList, null, sql, null,null,null,null);
		sitesDatas = new ListData<SociaxItem>();
		
		if(cursor == null){
			return null;
		}
		while(!cursor.isAfterLast()){
			ApproveSite sites = new ApproveSite();
			sites.setSite_id(cursor.getInt(cursor.getColumnIndex("site_id")));
			sites.setName(cursor.getString(cursor.getColumnIndex("name")));
			sites.setLogo(cursor.getString(cursor.getColumnIndex("logo")));
			cursor.moveToNext();
			sitesDatas.add(sites);
		}
		return sitesDatas;
	}
	
	//锟斤拷取锟斤拷前站锟斤拷
	public ApproveSite getInUsed(){
		Cursor cursor = sitesTable.getReadableDatabase().query(ThinksnsTableSqlHelper.siteList, null, "isused=1", null,null,null,null);
		if(cursor!=null){//锟轿标不为锟斤拷 
			ApproveSite site = new ApproveSite();
			if(cursor.moveToFirst()){
				site.setSite_id(cursor.getInt(cursor.getColumnIndex("site_id")));
				site.setName(cursor.getString(cursor.getColumnIndex("name")));
				site.setUrl(cursor.getString(cursor.getColumnIndex("url")));
				site.setLogo(cursor.getString(cursor.getColumnIndex("logo")));
	
			}else{
				cursor.close();
				return null;
			}
			return site;
		}
		return null;
	}
	
	//锟斤拷锟秸撅拷锟�	
	public void clearSite(){
		sitesTable.getWritableDatabase().execSQL("delete from "+ThinksnsTableSqlHelper.siteList);
	}
	//锟斤拷取锟斤拷站锟叫憋拷
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
