package com.thinksns.android;

import com.thinksns.constant.TSCons;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.NotifyCount;

//import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ThinksnsMessageService extends Service {
	
	private static final String TAG = "ThinksnsMessageService";
	
	private static NotifyCount notifyCount;
	
	private final static int ID = 3;
	MessageReceiver messageReceive;
	boolean flag;
	private final static int CMD_GET = 0;
	private final static int CMD_STOP=1;
	private final static int CMD_UNSET_ATME = 2;
	private final static int CMD_UNSET_COMMENT = 3;
	private final static int CMD_UNSET_MESSAGE = 4;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		super.onCreate();
	}

//	@TargetApi(5)
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		messageReceive = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.thinksns.android.ThinksnsMessageService");
		registerReceiver(messageReceive,filter);
		//getNotify();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void unsetNotifyCount(final NotifyCount.Type type){
		new Thread(){
			@Override
			public void run(){
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Thinksns app = (Thinksns)ThinksnsMessageService.this.getApplicationContext();
					try {
						app.getUsers().unsetNotificationCount(type, Thinksns.getMy().getUid());
					} catch (VerifyErrorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ListAreEmptyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DataInvalidException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
		}.start();
	}
	
	private void setNotifyCount(){
		new Thread(){
			@Override
			public void run(){
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Thinksns app = (Thinksns)ThinksnsMessageService.this.getApplicationContext();
					try {
						notifyCount = app.getUsers().notificationCount(Thinksns.getMy().getUid());
					} catch (VerifyErrorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ListAreEmptyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DataInvalidException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
		}.start();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		this.unregisterReceiver(messageReceive);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	private void sendNotify(int count){
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
		Intent intent = new Intent();
		intent.putExtra("tab", false);
		intent.setClass(ThinksnsMessageService.this, ThinksnsMessage.class);
        Notification notification = new Notification(R.drawable.icon,"新的信息", System.currentTimeMillis());
        PendingIntent pi = PendingIntent.getActivity(ThinksnsMessageService.this, 0, intent, 0);
        notification.setLatestEventInfo(ThinksnsMessageService.this, "", "你有新的"+count+"信息", pi);
        // 4.发送通知
        manager.notify(ID, notification);
	}
	
	private class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int cmd = intent.getIntExtra("cmd", -1);

			try {
				// 获取消息数
				if (cmd == CMD_GET) {
					new Thread() {
						public void run() {
							sendNotifyCount();
						}
					}.start();
					
				} else if (cmd == CMD_STOP) { // 停止服务
					ThinksnsMessageService.this.onDestroy();
				} else if (cmd == CMD_UNSET_ATME) {// ATME重制
					unsetNotifyCount(NotifyCount.Type.atMe);
				} else if (cmd == CMD_UNSET_COMMENT) {
					unsetNotifyCount(NotifyCount.Type.weibo_comment);
				} else if (cmd == CMD_UNSET_MESSAGE) {
					unsetNotifyCount(NotifyCount.Type.message);
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.d(TSCons.APP_TAG,
						"message service get notify wm " + e.toString());
			}
					
		}	
	}
	
	public NotifyCount getNotifyCount(){
		NotifyCount notifyCount = new NotifyCount();
		Thinksns app = (Thinksns)this.getApplicationContext();
		try {
			notifyCount = app.getUsers().notificationCount(Thinksns.getMy().getUid());
			Log.d(TAG,"getNotifyCount()---"+notifyCount.toString());
		} catch (VerifyErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ListAreEmptyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notifyCount;
	}
	
/*	public boolean unsetNotifyCount(NotifyCount.Type type){
		Thinksns app = (Thinksns)this.getApplicationContext();
		boolean success= false;
		try {
			success = app.getUsers().unsetNotificationCount(type, app.getMy().getUid());
		} catch (VerifyErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ListAreEmptyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}*/
	
	public void sendNotifyCount(){
		NotifyCount notifyCount = new NotifyCount();
		notifyCount = getNotifyCount();
		Intent intent = new Intent();
		intent.setAction("messageCount");
		Bundle bundle = new Bundle();
		bundle.putInt("atme", notifyCount.getAtme());
		bundle.putInt("myComment", notifyCount.getWeiboComment());
		
		bundle.putInt("message", notifyCount.getMessage());
		bundle.putInt("total", notifyCount.getTotal());
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
}
