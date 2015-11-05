package com.thinksns.android;


import org.json.JSONException;
import org.json.JSONObject;

import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.CommentListAdapter;
import com.thinksns.api.Api;
import com.thinksns.components.CommentList;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.RightIsButton;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.Comment;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.Weibo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ThinksnsWeiboComment extends ThinksnsAbscractActivity {
	private static final String TAG = "WeiboComment";
	private static Weibo weibo;
	private static CommentList list;
	private static CommentListAdapter adapter;
	private static ResultHandler resultHandler;
	private static Worker thread;
	private static ActivityHandler handler;
	private final static int DEL_COMMENT = 8;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thinksns app = (Thinksns)ThinksnsWeiboComment.this.getApplicationContext();
		resultHandler = new ResultHandler();
		try {
			weibo = new Weibo(new JSONObject(getIntentData().getString("data")));
		} catch (WeiboDataInvalidException e) {
			//app.getActivityStack().returnActivity(ThinksnsWeiboComment.this,getIntentData());
			ThinksnsWeiboComment.this.finish();
		} catch (JSONException e) {
		//	app.getActivityStack().returnActivity(ThinksnsWeiboComment.this,getIntentData());
			ThinksnsWeiboComment.this.finish();
		}
		
		
		// 获取list的布局对象
		list = (CommentList) findViewById(R.id.comment_list);
		// 获取数据源
		ListData<SociaxItem> data = new ListData<SociaxItem>();
		adapter = new CommentListAdapter(this, data,weibo);
		if(data.size() != 0){
			list.setAdapter(adapter,(long)adapter.getFirst().getTimestemp()*1000,this);
		}else{
			list.setAdapter(adapter,System.currentTimeMillis(),this);
		}
		adapter.loadInitData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		if(sendFlag){
			   refreshHeader();
			   sendFlag = false;
		}
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
		return R.layout.comment;
	}

	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return  new OnClickListener() {
			@Override
			public void onClick(View v) {
				getIntentData().putInt("send_type", COMMENT);
				Thinksns app = (Thinksns)ThinksnsWeiboComment.this.getApplicationContext();
				app.startActivity(ThinksnsWeiboComment.this,ThinksnsSend.class,getIntentData());
			}
		};
	}

	
	
	
	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return new RightIsButton(this,this.getString(R.string.comment));
	}
	
	@Override
	public int getRightRes() {
		return R.drawable.button_send;
	}


	@Override
	public String getTitleCenter() {
		return this.getString(R.string.comment_list);
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
	
	
	public void deleteComment(final Comment comment){
		AlertDialog.Builder builder = new Builder(this);
        final Activity obj = this;
        builder.setMessage("确定要删除此评论吗?"); 
        builder.setTitle("提示"); 
        builder.setPositiveButton("确认", 
                new android.content.DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                        dialog.dismiss(); 
                        Thinksns app = (Thinksns)obj.getApplicationContext();
                    	thread = new Worker(app,"statuses comment");
                		handler = new ActivityHandler(thread.getLooper(),app);
                		Message msg = handler.obtainMessage();
                		msg.obj = comment;
                		msg.what = DEL_COMMENT;
                		handler.sendMessage(msg);
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
	}
	
	private   class ActivityHandler extends Handler {
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			boolean newData = false;
			Message mainMsg = new Message();
			mainMsg.what = ResultHandler.ERROR;
			Thinksns app = (Thinksns)this.context.getApplicationContext();
			Api.Statuses status = app.getStatuses();
			try {
				if(msg.what == DEL_COMMENT){
					newData = status.destroyComment((Comment)msg.obj);
				}
				mainMsg.what = ResultHandler.SUCCESS;
				mainMsg.obj  = newData;
				mainMsg.arg1 = msg.what;
			} catch (VerifyErrorException e) {
				mainMsg.obj = e.getMessage();
				Log.e(TAG, e.getMessage());
			} catch (ApiException e) {
				mainMsg.obj = e.getMessage();
				Log.e(TAG, e.getMessage());
			} catch (DataInvalidException e) {
				mainMsg.obj = e.getMessage();
				Log.e(TAG, e.getMessage());
			}
			resultHandler.sendMessage(mainMsg);
		}
	}
	
	
	private class ResultHandler extends Handler{
		private static final int SUCCESS = 0;
		private static final int ERROR   = 1;
		
		
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			String info = "";
			if(msg.what == SUCCESS){
				if(msg.arg1 == DEL_COMMENT){
					info = "删除成功";
				}
			}else{
				info = (String)msg.obj;
			}
			Toast.makeText(ThinksnsWeiboComment.this,info, Toast.LENGTH_SHORT).show();
			if(msg.arg1 == DEL_COMMENT){
				Thinksns app = (Thinksns)ThinksnsWeiboComment.this.getApplicationContext();
				//app.getActivityStack().returnActivity(ThinksnsWeiboComment.this,getIntentData());
				ThinksnsWeiboComment.this.finish();
			}
		}
	}

}
