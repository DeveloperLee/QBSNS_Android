package com.thinksns.components;


import android.content.Context;
import android.util.AttributeSet;


public class SearchWeiboList extends WeiboList{

	public SearchWeiboList(Context context, AttributeSet attrs) {
		super(context, attrs);
	} 

	public SearchWeiboList(Context context) {
		super(context);
	}

	@Override
	protected void initDrag(Context context) {
		return ;
	}

	@Override
	protected void addHeaderView() {
		return ;
	}
	
	@Override
	public void headerRefresh() {
		return;
	}
	
}
