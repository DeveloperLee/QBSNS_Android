package com.thinksns.android;

import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.WeiboListAdapter;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.components.WeiboList;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ThinksnsMyWeibo extends ThinksnsAbscractActivity {
	
	private static final String TAG = "Home";

	private static WeiboListAdapter adapter;

	private static WeiboList list;
	

	private ThinksnsHome.MyWeiboAdapter mAdapter = new ThinksnsHome.MyWeiboAdapter() {
		
		@Override
		public void doAction() {
			// TODO Auto-generated method stub
			weiboHomeRefres();
		}
	};
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateDefault(savedInstanceState);
		
		ThinksnsHome.setMyWeiboAdapter(mAdapter);
		
		// 获取list的布局对象
		list = (WeiboList) findViewById(R.id.weiboList_home);
		// 获取数据源
		Thinksns app = (Thinksns) this.getApplicationContext();
		//app.getWeiboSql().deleteWeibo(30);
		//从服务器中得到初始化微博列表
		ListData<SociaxItem> tempWeiboDatas = app.getWeiboSql().selectWeibo(getSiteId(),Thinksns.getMy().getUid());
		ListData<SociaxItem> data = new ListData<SociaxItem>();
	    
		//将数据绑定到适配器上
		if(tempWeiboDatas.size() != 0){
			adapter = new WeiboListAdapter(this, tempWeiboDatas);
		}else{
			adapter = new WeiboListAdapter(this, data);
		}
		
		if(data.size()!=0 ||tempWeiboDatas.size() != 0){
			list.setAdapter(adapter,(long)adapter.getFirst().getTimestamp()*1000,this.getParent());
		}else{
			list.setAdapter(adapter,System.currentTimeMillis(),this.getParent());
		}
		
		adapter.loadInitData();
		int position = 0;
		Bundle temp = null;
		if((temp = getIntent().getExtras()) != null){
			position=temp.getInt("position");
		}
		list.setSelectionFromTop(position, 1);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Thinksns app = (Thinksns)this.getApplication();
		if(Thinksns.getDelIndex()>0){
			Log.e("app","app"+Thinksns.getDelIndex());
			adapter.deleteItem(Thinksns.getDelIndex());
			adapter.notifyDataSetChanged();
		}
		if(this.isInTab()) super.initTitle();
		
		if(sendFlag){
			refreshHeader();
			sendFlag = false ;
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
		adapter.isRefreshActivity="ThinksnsMyWeibo";
		adapter.doRefreshHeader();
	}
	
	@Override
	public void refreshFooter() {
		adapter.doRefreshFooter();
	}
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.home_weibo;
	}
   /**
    * 返回发布新微博的监听器
    */
	@Override
	public OnClickListener getLeftListener() {
		// TODO Auto-generated method stub
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Thinksns app = (Thinksns)ThinksnsMyWeibo.this.getApplicationContext();
				app.startActivity(ThinksnsMyWeibo.this.getParent(),ThinksnsCreate.class,null);
			}

		};
	}
	
	
	
	@Override
	public OnTouchListListener getListView(){
		return list;
	}
    /**
     * 返回主页刷新按钮监听器
     */
	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				weiboHomeRefres();
			}
		};
	}
	
	/*public static void refreshHeader() {
		this.refreshHeader();
		list.setSelectionFromTop(0, 20);
	}*/

	@Override
	public int getLeftRes() {
		return R.drawable.button_new;
	}

	@Override
	public int getRightRes() {
		return  R.drawable.button_refresh;
	}

	@Override
	public boolean isInTab() {
		return true;
	}
	
	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this);
	}
	
	@Override
	public String getTitleCenter() {
		if( null == Thinksns.getMy().getUserName()){
			Thinksns app = (Thinksns)this.getApplicationContext();
			try {
				Thinksns.setMy(app.getUserSql().getLoginedUser());
				 System.out.println("Thinksns.getMy().getUserName();"+Thinksns.getMy().getUserName());
			} catch (UserDataInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return  Thinksns.getMy().getUserName();
	}
	
	
	/*
	 * 展示大图
	 */
	
	@Override
	public OnClickListener getImageFullScreen(final String url){
			return new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getIntentData().putString("url", url);
					Thinksns app = (Thinksns)ThinksnsMyWeibo.this.getApplicationContext();
					app.startActivity(ThinksnsMyWeibo.this,ThinksnsImageView.class,getIntentData());				
				}
				
			};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == MYWEIBO_DEL){
			Toast.makeText(ThinksnsMyWeibo.this,data.getStringExtra("tips"), Toast.LENGTH_SHORT).show();
			list.removeViewAt(data.getIntExtra("weibo_index", 0));
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Thinksns app = (Thinksns)this.getApplicationContext();
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			 dialog(); 
	         return false; 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void weiboHomeRefres() {
		ThinksnsMyWeibo.this.refreshHeader();
		list.setSelectionFromTop(0, 20);
	}
	
}
