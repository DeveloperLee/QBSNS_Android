package com.thinksns.android;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.SearchWeiboListAdapter;
import com.thinksns.adapter.SociaxListAdapter;
import com.thinksns.adapter.UserListAdapter;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.EditCancel;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.components.SearchWeiboList;
import com.thinksns.components.SearchUserList;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;

public class ThinksnsSearch extends ThinksnsAbscractActivity implements OnKeyListener,OnCheckedChangeListener {
	
	private static final String TAG ="ThinksnsSearch";
	
	private static RadioButton searchWeibo;
	private static RadioButton searchUser;
	private static EditCancel edit;
	private static SociaxListAdapter adapter;
	private static Type status;
	private static final int LISTVIEW_ID = 186;
	
	private static SearchWeiboList weiboList;
	private static SearchUserList userList;
	private static Button goForSearch;
	private static LinearLayout layout;
	private static RadioGroup radioGroup;
	
	private enum Type{
		WEIBO,USER
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//super.onCreateDefault(savedInstanceState);
		
		//radioGroup=(RadioGroup)this.getParent().findViewById(R.id.main_radio);
		searchWeibo = (RadioButton)findViewById(R.id.search_weibo);
		searchUser  = (RadioButton)findViewById(R.id.search_user);
		edit        = (EditCancel)findViewById(R.id.editCancel1);
		layout      = (LinearLayout)findViewById(R.id.search_layout);
		goForSearch = (Button)findViewById(R.id.go_for_search);
		status = Type.WEIBO;
		searchWeibo.setOnCheckedChangeListener(this);
		searchUser.setOnCheckedChangeListener(this);
		edit.setOnKeyListener(this);
		
		goForSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				View oldList = layout.findViewById(LISTVIEW_ID);
				//隐藏输入法
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
				
				if(oldList != null){
					layout.removeView(oldList);
				}
				
				LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
				ListData<SociaxItem> data = new ListData<SociaxItem>();
				Log.d("searchkey", "searchkey"+edit.getText());
				Log.d(TAG, "searchkey"+edit.getText());
				switch(status){
				case WEIBO:
					
					// 获取list的布局对象
					weiboList = new SearchWeiboList(ThinksnsSearch.this);
					weiboList.setId(LISTVIEW_ID);
					weiboList.setLayoutParams(params);
					layout.addView(weiboList);
					// 获取数据源
					adapter = new SearchWeiboListAdapter(ThinksnsSearch.this, data,edit.getText());
					weiboList.setAdapter(adapter,System.currentTimeMillis(),ThinksnsSearch.this);
					adapter.loadInitData();
					break;
				case USER:
					// 获取list的布局对象
					userList = new SearchUserList(ThinksnsSearch.this);
					userList.setId(LISTVIEW_ID);
					userList.setLayoutParams(params);
					layout.addView(userList);
					// 获取数据源
					adapter = new UserListAdapter(ThinksnsSearch.this, data,edit.getText());
					userList.setAdapter(adapter,System.currentTimeMillis(),ThinksnsSearch.this);
					adapter.loadInitData();
					break;
				}
			}
		});
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event != null && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENVELOPE) && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_UP){
			View oldList = layout.findViewById(LISTVIEW_ID);
			
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
			if(oldList != null){
				layout.removeView(oldList);
			}
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			ListData<SociaxItem> data = new ListData<SociaxItem>();

			switch(status){
			case WEIBO:
				
				// 获取list的布局对象
				weiboList = new SearchWeiboList(ThinksnsSearch.this);
				weiboList.setId(LISTVIEW_ID);
				weiboList.setLayoutParams(params);
				layout.addView(weiboList);
				// 获取数据源
				adapter = new SearchWeiboListAdapter(this, data,edit.getText());
				weiboList.setAdapter(adapter,System.currentTimeMillis(),ThinksnsSearch.this);
				adapter.loadInitData();
				break;
			case USER:
				// 获取list的布局对象
				userList = new SearchUserList(ThinksnsSearch.this);
				userList.setId(LISTVIEW_ID);
				userList.setLayoutParams(params);
				layout.addView(userList);
				adapter = new UserListAdapter(this, data,edit.getText());
				userList.setAdapter(adapter,System.currentTimeMillis(),ThinksnsSearch.this);
				adapter.loadInitData();
				break;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public OnTouchListListener getListView(){
		Log.e("status","status"+status);
		switch (status){
		case WEIBO:
			return weiboList;
		case USER:
			return userList;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//if(this.isInTab()) super.initTitle();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	public String getTitleCenter() {
		return  this.getString(R.string.search);
	}
	

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this);
	}
	
	@Override
	public boolean isInTab() {
		return false;
	}

	
	@Override
	protected int getLayoutId() {
		return R.layout.search;
	}
	/*@Override
	public int getLeftRes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return 0;
	}*/
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			switch (buttonView.getId()) {
			case R.id.search_weibo:
				status = Type.WEIBO;
				break;
			case R.id.search_user:
				status = Type.USER;
				break;
			}
		}
	}
	
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Thinksns app = (Thinksns)this.getApplicationContext();
//		if(keyCode == KeyEvent.KEYCODE_BACK ){
//			 dialog(); 
//	         return false; 
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	
}





