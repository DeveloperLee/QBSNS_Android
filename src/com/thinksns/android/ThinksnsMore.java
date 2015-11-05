package com.thinksns.android;

import java.util.ArrayList; 
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.thinksns.api.Api;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.model.VersionInfo;
import com.thinksns.unit.UpdateManager;

public class ThinksnsMore extends ThinksnsAbscractActivity implements OnItemClickListener{

	private static final int CHECK_UPDATE = 1;
	private  UpdateManager mUpdateManager;
	private ActivityHandler handler = null;
	private ResultHandler result = null;
	private Worker thread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateDefault(savedInstanceState);
		ListView list = (ListView)findViewById(R.id.more_list);
		ArrayList<Map<String,Object>> data= new ArrayList<Map<String,Object>>();
		Map<String,Object> item;

		item=new HashMap<String,Object>();

		item.put("icon", R.drawable.icon_idea_32);

		item.put("text", "建议反馈");

		data.add(item);
		item=new HashMap<String,Object>();
		
		item.put("icon", R.drawable.icon_search_more_32);
		
		item.put("text", "搜索");
		
		data.add(item);
		
		item=new HashMap<String,Object>();
		
		item.put("icon", R.drawable.check_version);
		
		item.put("text", "检测新版本");
		
		data.add(item);
		item=new HashMap<String,Object>();

		item.put("icon", R.drawable.icon_exit_32);

		item.put("text", "退出系统");

		data.add(item);
		
		item = new HashMap<String,Object>();
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.tab_more_item, new String[] { "icon", "text" },
				new int[] { R.id.item_icon, R.id.item_text });


		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		Thinksns app = (Thinksns)this.getApplicationContext();
		thread = new Worker(app,"Home worker ");
		handler = new ActivityHandler(thread.getLooper(),app);
		result = new ResultHandler();
		mUpdateManager = new UpdateManager(this);  
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		
		super.onRestart();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(this.isInTab()) super.initTitle();
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
		return  this.getString(R.string.more);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this);
	}
	
	@Override
	public boolean isInTab() {
		return true;
	}

	
	@Override
	public int getLeftRes() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	protected int getLayoutId() {
		return R.layout.tab_more;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Thinksns app = (Thinksns)this.getApplicationContext();
		switch(position){
		
		case 0:
			getIntentData().putString("type", "suggest");
			app.startActivity(this.getParent(), ThinksnsCreate.class,getIntentData());
			break;
		case 1:
			//getIntentData().putString("type", "suggest");
			app.startActivity(this.getParent(), ThinksnsSearch.class,getIntentData());
			break;
		
		case 2:
			Message msg = handler.obtainMessage();
			msg.what = CHECK_UPDATE ;   //检查版本
			handler.sendMessage(msg);
			Toast.makeText(ThinksnsMore.this, R.string.check_version, Toast.LENGTH_LONG).show();
			break;
			
		case 3:
			 	AlertDialog.Builder builder = new Builder(this);
		        final Activity obj = this;
		        builder.setMessage("确定要注销退出吗?"); 
		        builder.setTitle("提示"); 
		        builder.setPositiveButton("确认", 
		                new android.content.DialogInterface.OnClickListener() { 
		                    @Override 
		                    public void onClick(DialogInterface dialog, int which) { 
		                        dialog.dismiss(); 
		                        Thinksns app = (Thinksns)obj.getApplicationContext();
		                        try {
									System.out.println("user " + app.getUserSql().getLoginedUser().toString());
								} catch (UserDataInvalidException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		                        app.getUserSql().clear();
		            			 ActivityManager activityManager =
		            			        (ActivityManager) ThinksnsMore.this.getParent().getSystemService(ThinksnsMore.this.getParent().ACTIVITY_SERVICE);
		            			        activityManager.restartPackage("com.thinksns.android");
		            			        System.exit(0);
		                    } 
		                }); 
		        builder.setNegativeButton("取消", 
		                new android.content.DialogInterface.OnClickListener() { 
		                    @Override 
		                    public void onClick(DialogInterface dialog, int which) { 
		                        dialog.dismiss(); 
		                    } 
		                }); 
		        builder.create().show(); 
			break;
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
			        if(mUpdateManager.checkUpdateInfo(vInfo) > 0){
			        	Toast.makeText(ThinksnsMore.this, R.string.no_version, Toast.LENGTH_LONG).show();
			        }
				}else if(msg.arg1 == 2){
					Toast.makeText(ThinksnsMore.this, R.string.check_version_error, Toast.LENGTH_LONG).show();
				}else if(msg.arg1 == 3){
					Toast.makeText(ThinksnsMore.this, R.string.no_version, Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	}
	
	
	
}
