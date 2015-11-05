package com.thinksns.android;


import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.MessageDetailAdapter;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.MessageDetail;
import com.thinksns.components.RightIsButton;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.Message;
import com.thinksns.model.SociaxItem;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

public class ThinksnsMessageDetail extends ThinksnsAbscractActivity {
	private static final String TAG = "MessageDetail";
	private static MessageDetail list;
	private static MessageDetailAdapter adapter;
 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// 获取list的布局对象
		list = (MessageDetail) findViewById(R.id.message_detail);
		// 获取数据源
		ListData<SociaxItem> data = new ListData<SociaxItem>();
		Message msg = new Message();
		msg.setListId(this.getIntentData().getInt("messageId"));
		adapter = new MessageDetailAdapter(this, msg,data);
		list.setAdapter(adapter,System.currentTimeMillis(),this);
		adapter.loadInitData();
		int position =0;
		Bundle temp = null;
		if((temp = getIntent().getExtras()) != null){
			position=temp.getInt("position");
		}
		list.setSelectionFromTop(position, 20);
	}


	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.message_detail;
	}

	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return  new OnClickListener() {
			@Override
			public void onClick(View v) {
				getIntentData().putInt("send_type", REPLY_MESSAGE);
				getIntentData().putInt("messageId", getIntentData().getInt("messageId"));
				Thinksns app = (Thinksns)ThinksnsMessageDetail.this.getApplicationContext();
				app.startActivity(ThinksnsMessageDetail.this,ThinksnsSend.class,getIntentData());
			}
		};
	}

	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return new RightIsButton(this,this.getString(R.string.reply));
	}
	
	@Override
	public int getRightRes() {
		return R.drawable.button_send;
	}


	@Override
	public String getTitleCenter() {
		return this.getString(R.string.message_detail);
	}
	
	@Override
	public OnTouchListListener getListView(){
		return list;
	}
	
	@Override
	public void refreshHeader() {
		adapter.doRefreshHeader();
	}
	
	@Override
	public void refreshFooter() {
		adapter.doRefreshFooter();
	}

    /**
     * 当页面回复时自动刷新列表，再将公共字段sendFlag设为false
     */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if(sendFlag){
		   refreshHeader();
		   sendFlag = false;
		}
	}
	  
	@Override
	protected void onPause(){
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
