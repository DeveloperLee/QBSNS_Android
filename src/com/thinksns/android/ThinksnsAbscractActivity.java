package com.thinksns.android;

import com.thinksns.components.CustomTitle;
import com.thinksns.constant.TSCons;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ApproveSite;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

public abstract class ThinksnsAbscractActivity extends Activity {
	public static final int TRANSPOND = 0;
	public static final int COMMENT   = 1;
	public static final int REPLY_MESSAGE = 2;
	public static final int CREATE_MESSAGE = 3;
	protected CustomTitle title;
	protected Bundle data;
	protected static final String TIPS = "tips";
	protected View mBtton;
	public static final int MYWEIBO_DEL = 1212;
	public static int button_count = 0;
	
	//公共字段 作用是判断是否页面回跳时自动刷新
	public static boolean sendFlag = false ;
	
	//获取中间
	public abstract String getTitleCenter();
	
	//获取左边资源
	public int getLeftRes(){
		return R.drawable.button_back;
	}
	//获取右边资源
	public int getRightRes(){
		return R.drawable.button_back_home;
	}
	
	//是否在底部tab中
	public boolean isInTab() {
		return false;
	}

	/**
	 * 设置头部
	 * @return
	 */
	protected abstract CustomTitle setCustomTitle();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setTheme(R.style.titleTheme);
		initCreate();
		initTitle();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		this.paramDatas();
		super.onResume();
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	protected void onCreateDefault(Bundle savedInstancesState){
		super.onCreate(savedInstancesState);
		initCreate();
		initTitle();
	}
	
	//初始化title
	protected void initTitle() {
		if(!this.isInTab()){
			title = this.setCustomTitle();
		}else{
			ThinksnsHome home = (ThinksnsHome)this.getParent();
			title = this.setCustomTitle();
			ThinksnsHome.setCustomTitle(title);
			home.setCurrentTabRight(this.getRightListener());
		}
	}
	
	//设置布局
	private void initCreate(){
		setContentView(this.getLayoutId());
		this.paramDatas();
		//Thinksns app = (Thinksns)this.getApplicationContext();
		//加入栈
		//app.getActivityStack().addCache(this);			
	}
	
	
	public  Bundle getIntentData(){
		if(data != null) return data;
		data = new Bundle();
		return data;
	}

	//看Intent数据中是否有TIPS，如果有,去除TIPS，用Toast显示TIPS
	protected void paramDatas() {
		data = this.getIntent().getExtras();
		if(data != null && data.containsKey(TIPS)){
			String tips = data.getString(TIPS);
			data.remove(TIPS);
			Toast.makeText(this, tips, Toast.LENGTH_SHORT).show(); 
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}

	//获取布局
	protected abstract int getLayoutId();
	
	//左部点击，加入栈
	public OnClickListener getLeftListener(){
		return new OnClickListener() {
					@Override
			public void onClick(View v) {
				Thinksns app = (Thinksns)ThinksnsAbscractActivity.this.getApplicationContext();
				ThinksnsAbscractActivity.this.finish();
				//app.getActivityStack().returnActivity(ThinksnsAbscractActivity.this,getIntentData());
			}
		};
	}
	//右边点击，清除栈，进入home
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Thinksns app = (Thinksns)ThinksnsAbscractActivity.this.getApplicationContext();
				//ActivityStack stack = app.getActivityStack();
				//stack.clear();
				//stack.startActivity(ThinksnsAbscractActivity.this,ThinksnsHome.class);
				Log.d(TSCons.APP_TAG, "ThinksnsAbscratActivity rigth title on click !!!");
				app.startActivity(ThinksnsAbscractActivity.this,ThinksnsHome.class,null);
			}
		};
	}

	
	@Override
	public void finish() {
		Thinksns app = (Thinksns)this.getApplicationContext();
		app.closeDb();
		super.finish();
	}
	
	public  void refreshHeader(){
		
	}
	
	public void refreshFooter(){
		
	}
	
	public OnTouchListListener getListView(){
		return null;
	}
	
	public CustomTitle getCustomTitle(){
		return title;
	}

	public OnClickListener getImageFullScreen(final String url){
		return new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getIntentData().putString("url", url);
				//getIntentData().putBoolean("tab", ThinksnsAbscractActivity.this.isInTab());
				Thinksns app = (Thinksns)ThinksnsAbscractActivity.this.getApplicationContext();
				//app.getActivityStack().startActivity(ThinksnsAbscractActivity.this,ThinksnsImageView.class,getIntentData());
				app.startActivity(ThinksnsAbscractActivity.this,ThinksnsImageView.class,getIntentData());	
			}
			
		};
}

	public int getSiteId(){
		Thinksns app = (Thinksns)this.getApplicationContext();
		ApproveSite as = Thinksns.getMySite();
		if(Thinksns.getMySite()==null){
			return 0;
		}else{
			return Thinksns.getMySite().getSite_id();
		}
	}

	protected void dialog() { 
	        AlertDialog.Builder builder = new Builder(this);
	        final Activity obj = this;
	        
	        builder.setTitle(R.string.dialog_alert);
	        builder.setMessage(R.string.exit); 
	        builder.setPositiveButton(R.string.dialog_ok, 
	                new android.content.DialogInterface.OnClickListener() { 
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        dialog.dismiss(); 
	                        obj.finish();
	                    } 
	                }); 
	        builder.setNegativeButton(R.string.dialog_cancel, 
	                new android.content.DialogInterface.OnClickListener() { 
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        dialog.dismiss(); 
	                    } 
	                }); 
	        builder.create().show(); 
	    }

	
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Thinksns app = (Thinksns)this.getApplicationContext();
//		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && app.getActivityStack().empty()){
//			 dialog(); 
//	         return false; 
//		}
//		return true;
//	}

	
}
