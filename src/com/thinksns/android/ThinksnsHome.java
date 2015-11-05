
package com.thinksns.android;

import java.util.HashMap;

import com.mobclick.android.MobclickAgent;
import com.thinksns.api.Api;
import com.thinksns.components.CustomTitle;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.model.NotifyCount;
import com.thinksns.model.VersionInfo;
import com.thinksns.unit.UpdateManager;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
/*
 * tab的载体是TabHost,需要从TabActivity.getTabHost获取
 */
public class ThinksnsHome extends TabActivity implements
		OnCheckedChangeListener ,View.OnClickListener {

	private TabHost mHost;
	
	private Intent mMBlogIntent;
	private Intent mMoreIntent;
	private Intent mInfoIntent;
	private Intent mSearchIntent;
	private Intent mUserInfoIntent;
	
	private static int[] radioCache;
	private static RadioButton[] mRadioButtons;
	
	private static CustomTitle customTitle;
	private static HashMap<String,OnClickListener> listener;
	
	private static Worker thread;
	private  NotifyCount notifyCount;
	private  TextView notifyText;
	
	private static final int ID = 99;
	private MessageCountReceiver messageCount;
	
	private int clickCountFlag = 0;
	
	private static MyWeiboAdapter mMyWeiboAdapter;
	
	private static final int CHECK_UPDATE = 1;
	private UpdateManager mUpdateManager;
	private ActivityHandler handler = null;
	private ResultHandler result = null;  //检查版本更新
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		MobclickAgent.update(this);
		
		Thinksns.allActivity.add(this);
		
		radioCache = new int[5];
		mRadioButtons = new RadioButton[5];
		
		radioCache[0] = R.id.radio_button0;
		radioCache[1] = R.id.radio_button1;
		radioCache[2] = R.id.radio_button2;
		radioCache[3] = R.id.radio_button3;
		radioCache[4] = R.id.radio_button4;
		
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setTheme(R.style.titleTheme);
		setContentView(R.layout.home);
		Window window = this.getWindow();
		window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		

		notifyText = (TextView)findViewById(R.id.message_notice);
		listener = new  HashMap<String,OnClickListener> ();
		//初始化主界面下部的Tabhost
		this.mMBlogIntent = new Intent(ThinksnsHome.this, ThinksnsMyWeibo.class);
		this.mSearchIntent = new Intent(this, ThinksnsSquare.class);
		this.mInfoIntent = new Intent(this, ThinksnsMessage.class);
		this.mUserInfoIntent = new Intent(this, ThinksnsUserInfo.class);
		this.mMoreIntent = new Intent(this, ThinksnsMore.class);
		this.mUserInfoIntent.putExtra("tab", true);
		
		initRadios();
		setupIntent();
		Intent myIntent = new Intent();
	    myIntent.setClass(ThinksnsHome.this, ThinksnsMessageService.class);
	    ThinksnsHome.this.startService(myIntent);
	    
	    clickCountFlag ++;
	    
	    Thinksns app = (Thinksns)this.getApplicationContext();
		thread = new Worker(app,"Home worker ");
		handler = new ActivityHandler(thread.getLooper(),app);
		result = new ResultHandler();
		mUpdateManager = new UpdateManager(this);  
		
		Message msg = handler.obtainMessage();
		msg.what = CHECK_UPDATE ;   //检查版本
		handler.sendMessage(msg);
        
        
        
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
	
	public void setCurrentTabRight(OnClickListener listener){
		ThinksnsHome.listener.put(this.mHost.getCurrentTabTag(), listener);
	}

	
	/**
	 * 初始化底部按钮
	 */
	private void initRadios() {
		for(int i=0;i<5;i++){
			((RadioButton) findViewById(radioCache[i])).setOnCheckedChangeListener(this);
			((RadioButton) findViewById(radioCache[i])).setOnClickListener(this);
			mRadioButtons[i] = (RadioButton) findViewById(radioCache[i]);
		}
	}
	
	/**
	 * 切换模块
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				this.mHost.setCurrentTabByTag("mblog_tab");		
				clickCountFlag = 0;
				break;
			case R.id.radio_button1:
				this.mHost.setCurrentTabByTag("message_tab");
				clickCountFlag = 0;
				break;
			case R.id.radio_button2:
				this.mHost.setCurrentTabByTag("userinfo_tab");
				clickCountFlag = 0;
				break;
			case R.id.radio_button3:
				this.mHost.setCurrentTabByTag("search_tab");
				clickCountFlag = 0;
				break;
			case R.id.radio_button4:
				this.mHost.setCurrentTabByTag("more_tab");
				clickCountFlag = 0;
				break;
			}
			//头部重建
			//this.getCustomTitle().getRight().setOnClickListener(ThinksnsHome.listener.get(this.mHost.getCurrentTabTag()));
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(mRadioButtons[0])){
			clickCountFlag ++ ;
			if(mRadioButtons[0].isChecked() && clickCountFlag > 1){
				mMyWeiboAdapter.doAction();
				System.out.println( "one tab btn click more ...");
			}
		}
	}
	

	@Override
	protected void onStart() {
		//注册广播
		messageCount = new MessageCountReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("messageCount");
		registerReceiver(messageCount,filter);
		super.onStart();
	}

	private void setupIntent() {
		this.mHost = getTabHost();
		TabHost localTabHost = this.mHost;
		localTabHost.addTab(buildTabSpec("mblog_tab", R.string.info,
				R.drawable.home_1, this.mMBlogIntent));

		localTabHost.addTab(buildTabSpec("message_tab", R.string.info,
				R.drawable.message_1, this.mInfoIntent));

		localTabHost.addTab(buildTabSpec("userinfo_tab", R.string.info,
				R.drawable.myinfo_1, this.mUserInfoIntent));

		localTabHost.addTab(buildTabSpec("search_tab", R.string.info,
				R.drawable.search_1, this.mSearchIntent));

		localTabHost.addTab(buildTabSpec("more_tab", R.string.info,
				R.drawable.more_1, this.mMoreIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	public static CustomTitle getCustomTitle() {
		return customTitle;
	}

	public static void setCustomTitle(CustomTitle customTitle) {
		ThinksnsHome.customTitle = customTitle;
	}
	
	public int getCurrentTabId(){
		return this.mHost.getCurrentTab();
	}
	
	@Override
	protected void onStop() {
		ThinksnsHome.setCustomTitle(null);
		unregisterReceiver(messageCount);
		super.onStop();
	}
	
	private  class ActivityHandler extends Handler{
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Message reMsg = result.obtainMessage();
			switch (msg.what) {
			case CHECK_UPDATE:
				Api.Upgrade aUpgrade = new Api.Upgrade();
				try {
					VersionInfo vInfo = aUpgrade.getVersion();
					reMsg.obj = vInfo ;
					reMsg.what = msg.what;
					reMsg.arg1 = 1;
				} catch (ApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					reMsg.arg1 = 2;
				}
				break;
			}
			result.sendMessage(reMsg);
		}
	}
	
	private class ResultHandler extends Handler{
		@Override
		public  void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_UPDATE:
				if(msg.arg1 == 1){
					VersionInfo vInfo = (VersionInfo) msg.obj;
					//检查版本
			        mUpdateManager.checkUpdateInfo(vInfo);
				}
				break;
			}
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
	/*    Intent cmdIntent = new Intent();
	    cmdIntent.setAction("com.thinksns.android.ThinksnsMessageService");
	    cmdIntent.putExtra("cmd", 1);
	    sendBroadcast(cmdIntent);*/
		super.finish();
	}

	private class MessageCountReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			System.out.println("ThinksnsHome---onReceive");
			// TODO Auto-generated method stub
			Log.e("mess","msee"+intent.getAction());
			Bundle bundle = intent.getExtras();
			int atme = bundle.getInt("atme");
			int myComment = bundle.getInt("myComment");
			
			int message = bundle.getInt("message");
			int total = bundle.getInt("total");
			if(total>0){
				if(atme>0){
					Toast.makeText(context,"有人@你", Toast.LENGTH_SHORT).show();
				}
				if(myComment>0){
					Toast.makeText(context,"你有新的评论", Toast.LENGTH_SHORT).show();
				}
				if(message>0){
					Toast.makeText(context,"你有新的私信", Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	}

	public void exitApp() {
		// TODO Auto-generated method stub
		this.finish();
	}

	 public static void setMyWeiboAdapter( MyWeiboAdapter paramMyWeiboAdapter)
	  {
	    mMyWeiboAdapter = paramMyWeiboAdapter;
	  }

	  public static abstract interface MyWeiboAdapter
	  {
	    public abstract void doAction();
	  }
	
}
