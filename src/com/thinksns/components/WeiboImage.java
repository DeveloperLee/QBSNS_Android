package com.thinksns.components;

import android.graphics.Bitmap;
import android.view.View;

import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.model.Weibo;
import com.thinksns.unit.WeiboDataSet;

public class WeiboImage extends WeiboDataSet {

	@Override
	public View appendTranspond(Weibo weibo, View view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void appendWeiboData(Weibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getContentIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getGravity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Bitmap getThumbCache(Weibo weibo) {
		// TODO Auto-generated method stub
		return weibo.getThumbLarge();
	}

	@Override
	protected  BitmapDownloaderTask.Type getThumbType(){
		return BitmapDownloaderTask.Type.LARGE_THUMB;
	}
	
	@Override
	protected  boolean hasThumbCache(Weibo weibo){ 
		return weibo.isNullForThumbLargeCache();
	}
	
	@Override
	protected  String getThumbUrl(Weibo weibo){
		return weibo.getPicUrl();
	}
	
	@Override
	public void setCommentCount(Weibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setCountLayout(Weibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTranspondCount(Weibo weibo, View view) {
		// TODO Auto-generated method stub

	}

}
