package com.thinksns.components;

import android.content.Context;
import android.view.View;

public class SearchUserList extends FollowList {

	public SearchUserList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onClick(View view, int position, long id) {
		// TODO Auto-generated method stub
		super.onClick(view, position, id);
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
