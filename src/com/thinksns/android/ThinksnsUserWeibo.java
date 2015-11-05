package com.thinksns.android;

import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.UserWeiboListAdapter;
import com.thinksns.adapter.WeiboListAdapter;
import com.thinksns.api.Api;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.components.WeiboList;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import android.os.Bundle;
import android.view.View.OnClickListener;

public class ThinksnsUserWeibo extends ThinksnsAbscractActivity {

	private static WeiboListAdapter adapter;

	private static WeiboList list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 获取list的布局对象
		list = (WeiboList) findViewById(R.id.weiboList_home);

		// 获取数据源
		Thinksns app = (Thinksns) this.getApplicationContext();
		Api.Statuses api = app.getStatuses();
		ListData<SociaxItem> data = new ListData<SociaxItem>();
		adapter = new UserWeiboListAdapter(this, data,getIntentData().getInt("uid"));
		if(data.size() != 0){
			list.setAdapter(adapter,(long)adapter.getFirst().getTimestamp()*1000,this);
		}else{
			list.setAdapter(adapter,System.currentTimeMillis(),this);
		}
		adapter.loadInitData();
		int position = 0;
		Bundle temp = null;
		if((temp = getIntent().getExtras()) != null){
			position=temp.getInt("position");
		}
		//list.setSelection(position);
		list.setSelectionFromTop(position, 1);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		if(sendFlag){
 		  adapter.doRefreshHeader();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void refreshHeader() {
		adapter.doRefreshHeader();
	}
	
	@Override
	public void refreshFooter() {
		adapter.doRefreshFooter();
	}
	


	@Override
	protected int getLayoutId() {
		return R.layout.userweibo;
	}
	
	@Override
	public OnTouchListListener getListView(){
		return list;
	}
	
	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return super.getRightListener();
	}

/*	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				ThinksnsUserWeibo.this.refreshHeader();
			}
		};
	}
	*/
	@Override
	protected CustomTitle setCustomTitle() {
		return 	new LeftAndRightTitle(this);
	}

	@Override
	public String getTitleCenter() {
		return getIntentData().getString("uname");
	}
	
}
