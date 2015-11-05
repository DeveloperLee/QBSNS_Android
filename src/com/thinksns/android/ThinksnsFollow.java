package com.thinksns.android;


import org.json.JSONException;
import org.json.JSONObject;

import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.FollowListAdapter;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.FollowList;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 显示粉丝列表的Activity
 * @author Administrator
 *
 */
public class ThinksnsFollow extends ThinksnsAbscractActivity {
	private static final String TAG = "FollowList";
	private static User user;
	private static FollowList list;
	private static FollowListAdapter adapter;
	private static final int ADD_FOLLOWED = 0;
	private static final int DEL_FOLLOWED = 1;
	private static int userType ;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getIntentData().putBoolean("tab", false);
		// 获取list的布局对象
		list = (FollowList)findViewById(R.id.follow_list);
		Thinksns app = (Thinksns)ThinksnsFollow.this.getApplicationContext();
		try {
			user = new User(new JSONObject(getIntentData().getString("data")));
			Log.e("user","user="+user.toJSON());
		} catch (WeiboDataInvalidException e) {
			ThinksnsFollow.this.finish();
		} catch (JSONException e) {
			ThinksnsFollow.this.finish();
		} catch (DataInvalidException e) {
			ThinksnsFollow.this.finish();
		}
		// 获取数据源
		ListData<SociaxItem> data = new ListData<SociaxItem>();
		userType = getIntentData().getInt("type");
		adapter = new FollowListAdapter(this, data, getIntentData().getInt("type"), user);
		list.setAdapter(adapter,System.currentTimeMillis(),this);
		int position = 0;
		Bundle temp = null;
		adapter.loadInitData();
		if((temp = getIntent().getExtras()) != null){
			position=temp.getInt("position");
		}
		list.setSelectionFromTop(position, 1);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.follow;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return 	new LeftAndRightTitle(this);
	}
	
	@Override
	public String getTitleCenter() {
		//修改关注和粉丝显示交叉
		userType = getIntentData().getInt("type");
		
		if(userType == 1){
			return  this.getString(R.string.followed);
		}else{
			return  this.getString(R.string.follow);
		}
		
	}
	

/*	@Override
	public int getRightRes() {
		return  R.drawable.button_refresh;
	}
	*/
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
	 * OnKeyDown方法必须重写 否则无法动态刷新尾部
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			 dialog(); 
	         return false; 
		}
		return super.onKeyDown(keyCode, event);
	}
	
}




