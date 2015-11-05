package com.thinksns.android;

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
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.PublicWeiboListAdapter;
import com.thinksns.api.Api;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.components.WeiboList;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.VersionInfo;
import com.thinksns.unit.UpdateManager;

public class ThinksnsSquare extends ThinksnsAbscractActivity{

	//private static final int CHECK_UPDATE = 1;
	//private UpdateManager mUpdateManager;
	//private ActivityHandler handler = null;
	//private ResultHandler result = null;
	//private Worker thread;

	private static PublicWeiboListAdapter adapter;

	private static WeiboList list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateDefault(savedInstanceState);

		// 获取list的布局对象
		list = (WeiboList) findViewById(R.id.weibo_list_square);

		// 获取数据源
		ListData<SociaxItem> data = new ListData<SociaxItem>();
		adapter = new PublicWeiboListAdapter(this, data);
		if (data.size() != 0) {
			list.setAdapter(adapter,
					(long) adapter.getFirst().getTimestamp() * 1000, this);
		} else {
			list.setAdapter(adapter, System.currentTimeMillis(), this);
		}
		adapter.loadInitData(); //loadInitData()是向adpater中的list填充微博信息
		int position = 0;
		Bundle temp = null;
		if ((temp = getIntent().getExtras()) != null) {
			position = temp.getInt("position");
		}
		// list.setSelection(position);
		list.setSelectionFromTop(position, 1);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (this.isInTab())
			super.initTitle();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public String getTitleCenter() {
		return this.getString(R.string.square);
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
		return 0;
	}

	@Override
	public int getRightRes() {
		return 0;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.square;
	}

	@Override
	public OnTouchListListener getListView() {
		// TODO Auto-generated method stub
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
	
	/*@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Thinksns app = (Thinksns) this.getApplicationContext();
		switch (position) {
		
		case 1:
			getIntentData().putString("type", "suggest");
			app.startActivity(this.getParent(), ThinksnsCreate.class,
					getIntentData());
			break;

		case 2:
			Message msg = handler.obtainMessage();
			msg.what = CHECK_UPDATE; // 检查版本
			handler.sendMessage(msg);
			Toast.makeText(ThinksnsSquare.this, R.string.check_version,
					Toast.LENGTH_LONG).show();
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
							Thinksns app = (Thinksns) obj
									.getApplicationContext();
							app.getUserSql().clear();
							ActivityManager activityManager = (ActivityManager) ThinksnsSquare.this
									.getParent()
									.getSystemService(
											ThinksnsSquare.this.getParent().ACTIVITY_SERVICE);
							activityManager
									.restartPackage("com.thinksns.android");
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
		Thinksns app = (Thinksns) this.getApplicationContext();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class ActivityHandler extends Handler {
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
					reMsg.obj = vInfo;
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

	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_UPDATE:
				if (msg.arg1 == 1) {
					VersionInfo vInfo = (VersionInfo) msg.obj;
					// 检查版本
					if (mUpdateManager.checkUpdateInfo(vInfo) > 0) {
						Toast.makeText(ThinksnsSquare.this,
								R.string.no_version, Toast.LENGTH_LONG).show();
					}
				} else if (msg.arg1 == 2) {
					Toast.makeText(ThinksnsSquare.this,
							R.string.check_version_error, Toast.LENGTH_LONG)
							.show();
				} else if (msg.arg1 == 3) {
					Toast.makeText(ThinksnsSquare.this, R.string.no_version,
							Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	}*/

}
