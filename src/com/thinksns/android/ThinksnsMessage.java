package com.thinksns.android;

//import android.content.BroadcastReceiver;
//import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.AtomAdapter;
import com.thinksns.adapter.CommentMyListAdapter;
import com.thinksns.adapter.MessageInboxListAdapter;
import com.thinksns.adapter.SociaxListAdapter;
import com.thinksns.components.CommentMyList;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.MessageInboxList;
import com.thinksns.components.OnlyCenterTitle;
import com.thinksns.components.WeiboList;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.components.SociaxList;


/**
 * 显示@，评论和私信的Activity
 * @author Administrator
 *
 */
public class ThinksnsMessage extends ThinksnsAbscractActivity {
	private static SociaxList list;
	private static SociaxListAdapter adapter;

	private static WeiboList atom;
	private static CommentMyList comment;
	private static MessageInboxList message;
	
	private static AtomAdapter atomAdapter;
	private static CommentMyListAdapter commentAdapter;
	private static MessageInboxListAdapter messageAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(this.isInTab()){
			super.onCreateDefault(savedInstanceState);
		}else{
			super.onCreate(savedInstanceState);
		}
		atom = (WeiboList)findViewById(R.id.atom);
		comment=(CommentMyList)findViewById(R.id.comment);
		message=(MessageInboxList)findViewById(R.id.msg_inbox);
		
		
		Thinksns app = (Thinksns) this.getApplicationContext();
		
		 //@我
		ListData<SociaxItem> data = new ListData<SociaxItem>();
		ListData<SociaxItem> tempAtMeDatas = app.getAtMeSql().selectWeibo(getSiteId(),Thinksns.getMy().getUid());
		if(tempAtMeDatas.size() != 0){
			atomAdapter = new AtomAdapter(this, tempAtMeDatas);
		}else{
			atomAdapter = new AtomAdapter(this, data);
		}
		atom.setAdapter(atomAdapter,System.currentTimeMillis(),this.getParent());

		//评论
		ListData<SociaxItem> data2 = new ListData<SociaxItem>();
		ListData<SociaxItem> tempAtMeDatas2 = app.getMyCommentSql().selectComment(getSiteId(),Thinksns.getMy().getUid());
		if(tempAtMeDatas.size() != 0){
			commentAdapter = new CommentMyListAdapter(this, tempAtMeDatas2);
		}else{
			commentAdapter = new CommentMyListAdapter(this, data2);
		}
		comment.setAdapter(commentAdapter,System.currentTimeMillis(),ThinksnsMessage.this.getParent());
		
		//私信
		ListData<SociaxItem> data3 = new ListData<SociaxItem>();
		ListData<SociaxItem> tempAtMeDatas3 = app.getMyMessageSql().selectMessage();
		if(tempAtMeDatas.size() != 0){
			messageAdapter = new MessageInboxListAdapter(this, tempAtMeDatas3);
		}else{
			messageAdapter = new MessageInboxListAdapter(this, data3);
		}		
		message.setAdapter(messageAdapter,System.currentTimeMillis(),this.getParent());
		adapter = atomAdapter;
		list = atom;
		atomAdapter.loadInitData();
		int position = 0;
		Bundle temp = null;
		if((temp = getIntent().getExtras()) != null){
			position=temp.getInt("position");
		}
		list.setSelectionFromTop(position, 20);
	}
	
	@Override
	public OnTouchListListener getListView(){
		return list;
	}
	
	@Override
	protected void initTitle() {
		title = this.setCustomTitle();
	}
	

	public OnClickListener getButtonListener(int what,final Button button1,final Button button2,final Button button3){
		switch (what){
		case 0://@我的
			return new OnClickListener(){
				@Override
				public void onClick(View v) {
					button_count = 0;
					message.setVisibility(View.GONE);
					comment.setVisibility(View.GONE);
					v.setBackgroundResource(R.drawable.qie_nav_bg);
					button1.setBackgroundResource(R.drawable.qie_bg_02);
					button2.setBackgroundResource(R.drawable.qie_bg_03);
					list = atom;
					adapter = atomAdapter;
					if(atom.getVisibility() ==View.GONE){
						atom.setVisibility(View.VISIBLE);
						adapter.loadInitData();
					}
				}
			};
		case 1://评论我的
			return new OnClickListener(){

				@Override
				public void onClick(View v) {
					button_count = 1;
					atom.setVisibility(View.GONE);
					message.setVisibility(View.GONE);
					v.setBackgroundResource(R.drawable.qie_nav_bg);
					button1.setBackgroundResource(R.drawable.qie_bg_01);
					button2.setBackgroundResource(R.drawable.qie_bg_03);
					list = comment;
					adapter = commentAdapter;
					if(comment.getVisibility() == View.GONE){
						comment.setVisibility(View.VISIBLE);
						adapter.loadInitData();
					}
				}
				
			};
		case 2://收件箱
			return new OnClickListener(){
				@Override
				public void onClick(View v) {
					button_count = 2;
					atom.setVisibility(View.GONE);
					comment.setVisibility(View.GONE);
					v.setBackgroundResource(R.drawable.qie_nav_bg);
					button1.setBackgroundResource(R.drawable.qie_bg_01);
					button2.setBackgroundResource(R.drawable.qie_bg_02);
					list = message;
					adapter = messageAdapter;
					if(message.getVisibility() == View.GONE){
						message.setVisibility(View.VISIBLE);
						adapter.loadInitData();
					}
				}
				
			};
		}
		return null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		switch (button_count) {
		case 0:
			message.setVisibility(View.GONE);
			comment.setVisibility(View.GONE);
			atom.setVisibility(View.VISIBLE);
			break;
		case 1:
			message.setVisibility(View.GONE);
			comment.setVisibility(View.VISIBLE);
			atom.setVisibility(View.GONE);
			break;
		case 2:
			message.setVisibility(View.VISIBLE);
			comment.setVisibility(View.GONE);
			atom.setVisibility(View.GONE);
		default:
			break;
		}
	    Intent cmdIntent = new Intent();
	    cmdIntent.setAction("com.thinksns.android.ThinksnsMessageService");
	    cmdIntent.putExtra("cmd", 0);
	    sendBroadcast(cmdIntent);
		if(this.isInTab()) super.initTitle();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public String getTitleCenter() {
		return "@我,评论,私信";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return 	new OnlyCenterTitle(this,button_count);
	}
	
	@Override
	public boolean isInTab() {
		return  this.getIntent().getBooleanExtra("tab", true);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.message;
	}
	
	
	@Override
	public void refreshHeader() {
		adapter.doRefreshHeader();
	}
	
	
	@Override
	public void refreshFooter() {
		adapter.doRefreshFooter();
	}

//	private class unsetMessageReceiver extends BroadcastReceiver{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			
//		}
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Thinksns app = (Thinksns)this.getApplicationContext();
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			 dialog(); 
	         return false; 
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
