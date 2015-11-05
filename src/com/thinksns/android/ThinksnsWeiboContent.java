package com.thinksns.android;


import org.json.JSONException;
import org.json.JSONObject;

import com.mobclick.android.MobclickAgent;
import com.thinksns.api.Api;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.model.Weibo;
import com.thinksns.unit.Anim;
import com.thinksns.unit.WeiboContent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.text.ClipboardManager;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class ThinksnsWeiboContent extends ThinksnsAbscractActivity implements OnTouchListener,
OnGestureListener {
	private static final String TAG = "WeiboContent";
	private static Weibo weibo;
	private static final int ADD_FAVORITE = 0;
	private static final int DEL_FAVORITE = 1;
	private static final int DEL_WEIBO  =2;
	private static final int REFRESH=3;
	private static ImageButton favorite;
	private static ResultHandler resultHandler;
	private static Worker thread;
	private static ActivityHandler handler;
	
	GestureDetector mGestureDetector;
	private LinearLayout frameLayout;
	private ScrollView scrollView;
	
	public enum FavoriteStatus{
		YES,NO
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
		resultHandler = new ResultHandler();
		
		mGestureDetector = new GestureDetector(this);
		
		frameLayout = (LinearLayout) findViewById(R.id.weibo_data);
		scrollView =  (ScrollView) findViewById(R.id.scroll_weibo_data);
		scrollView.setOnTouchListener(this);
		scrollView.setLongClickable(true);
		
		try {
			weibo = new Weibo(new JSONObject(getIntentData().getString("data")));
			this.setWeiboContentData(weibo);
			this.setClickListener();
			this.setButtomClickListener();
		} catch (WeiboDataInvalidException e) {
			//app.getActivityStack().returnActivity(ThinksnsWeiboContent.this,getIntentData());
			ThinksnsWeiboContent.this.finish();
		} catch (JSONException e) {
			//app.getActivityStack().returnActivity(ThinksnsWeiboContent.this,getIntentData());
			ThinksnsWeiboContent.this.finish();
		}
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
	/*
	 * 底部按钮设置
	 */
	private void setButtomClickListener(){
		ImageButton contentTransport = (ImageButton)findViewById(R.id.forward_image_button);
		contentTransport.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getIntentData().putInt("send_type", TRANSPOND);
				Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
				app.startActivity(ThinksnsWeiboContent.this,ThinksnsSend.class,getIntentData());
			}
		});	
		
		ImageButton contentComment = (ImageButton)findViewById(R.id.comment_image_button);
		contentComment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getIntentData().putInt("send_type", COMMENT);
				Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
				app.startActivity(ThinksnsWeiboContent.this,ThinksnsSend.class,getIntentData());
			}
		});	
		
		ImageButton contentCopy = (ImageButton)findViewById(R.id.copy_image_button);
		contentCopy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(weibo.getContent());
				Toast.makeText(ThinksnsWeiboContent.this,"复制成功", Toast.LENGTH_SHORT).show();
			}
		});			
		
		ImageButton contentMore = (ImageButton)findViewById(R.id.more_image_button);
		contentMore.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ThinksnsWeiboContent.this.openOptionsMenu();
			}
		});	
	}
	/*
	 * 收藏、转发、评论按钮
	 */
	private void setClickListener() {
		Button transpond = (Button)findViewById(R.id.button_transpond);
		transpond.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getIntentData().putInt("send_type", TRANSPOND);
				Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
				app.startActivity(ThinksnsWeiboContent.this,ThinksnsSend.class,getIntentData());
			}
		});
		
		Button comment = (Button)findViewById(R.id.button_comment);
		comment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getIntentData().putInt("send_type", COMMENT);
				Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
				app.startActivity(ThinksnsWeiboContent.this,ThinksnsWeiboComment.class,getIntentData());
			}
		});
		
		RelativeLayout layout= (RelativeLayout)findViewById(R.id.userinfo);
		layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getIntentData().putInt("uid", weibo.getUid());
				Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
				app.startActivity(ThinksnsWeiboContent.this,ThinksnsUserInfo.class,getIntentData());
			}
		});
		
		favorite = (ImageButton)findViewById(R.id.button_favorite);
		favorite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
				thread = new Worker(app,"statuses weibo");
				handler = new ActivityHandler(thread.getLooper(),app);
				Message msg = handler.obtainMessage();
				msg.obj = weibo;
				switch((FavoriteStatus) v.getTag()){
				case YES:
					msg.what = DEL_FAVORITE;
					favorite.setImageResource(R.drawable.button_favorite);
					break;
				case NO:
					msg.what = ADD_FAVORITE;
					favorite.setImageResource(R.drawable.button_is_favorite);
					break;
				}
				favorite.setClickable(false);
				handler.sendMessage(msg);
			}
		});
	}

	//增加内容
	private void setWeiboContentData(Weibo weibo){
		WeiboContent helper = new WeiboContent(this);
	//	WeiboDataItem weiboDataItem = new WeiboDataItem();
		helper.appendWeiboData(weibo, this.findViewById(R.id.weibo_content_layout));
		
	}
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.weibocontent;
	}


	@Override
	protected CustomTitle setCustomTitle() {
		return 	new LeftAndRightTitle(this);
	}
	
	@Override
	public String getTitleCenter() {
		return  this.getString(R.string.weibo_content);
	}

	@Override
	public void openOptionsMenu() {
		// TODO Auto-generated method stub
		super.openOptionsMenu();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
		if(Thinksns.getMy().getUid() == weibo.getUid() ){
			menu.add(0, 0, 0,"删除");
		}
		   menu.add(1,1,0,"刷新");
		   menu.add(2, 2, 0,"取消");
		
		   return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		final Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
		switch(id){
			case 0:
				
				AlertDialog.Builder builder = new Builder(this);
		        final Activity obj = this;
		        builder.setMessage("确定要删除此微博吗?"); 
		        builder.setTitle("提示"); 
		        builder.setPositiveButton("确认", 
		                new android.content.DialogInterface.OnClickListener() { 
		                    @Override 
		                    public void onClick(DialogInterface dialog, int which) { 
		                        dialog.dismiss(); 
		                		thread = new Worker(app,"delete opt");
		        				handler = new ActivityHandler(thread.getLooper(),app);
		        				Message msg = handler.obtainMessage();
		        				msg.obj = weibo;
		        				msg.what = DEL_WEIBO;
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
				break;
			case 1:
				thread = new Worker(app,"refresh opt");
				handler = new ActivityHandler(thread.getLooper(),app);
				Message msg1 = handler.obtainMessage();
				msg1.obj = weibo;
				msg1.what = REFRESH;
				handler.sendMessage(msg1);
				break;
			case 2:
				closeOptionsMenu();
		}
		return true;
	}

	@Override
	public void closeOptionsMenu() {
		// TODO Auto-generated method stub
		super.closeOptionsMenu();
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
			Weibo newWeibo = new Weibo();
			Thinksns app = (Thinksns)this.context.getApplicationContext();
			Api.Favorites favorites = app.getFavorites();
			Api.Statuses status = app.getStatuses();
			try {
				switch(msg.what){
				case ADD_FAVORITE:
					newData = favorites.create((Weibo)msg.obj);
					break;
				case DEL_FAVORITE:
					newData = favorites.destroy((Weibo)msg.obj);
					break;
				case DEL_WEIBO:
					newData = status.destroyWeibo((Weibo)msg.obj);
					app.getWeiboSql().deleteOneWeibo(((Weibo)msg.obj).getWeiboId());
					break;
				case REFRESH:
					newWeibo = status.show(((Weibo)msg.obj).getWeiboId());
				}
				if(msg.what == REFRESH){
					mainMsg.obj = newWeibo;
				}else{
					mainMsg.obj  = newData;
				}
				mainMsg.what = ResultHandler.SUCCESS;
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
				Log.e("ms","ms"+msg.arg1);
				switch (msg.arg1){
				
				case ADD_FAVORITE:
					favorite.setTag(ThinksnsWeiboContent.FavoriteStatus.YES);
					info = "收藏成功";
					break;
				case DEL_FAVORITE:
					favorite.setTag(ThinksnsWeiboContent.FavoriteStatus.NO);
					info = "取消收藏成功";
					break;
				case DEL_WEIBO:
					info = "删除成功";
					sendFlag = true;
					break;
				case REFRESH:
					ThinksnsWeiboContent.this.setWeiboContentData((Weibo)msg.obj);
					info = "刷新成功";
				}
			}else{
				info = (String)msg.obj;
			}
			Toast.makeText(ThinksnsWeiboContent.this,info, Toast.LENGTH_SHORT).show();
			if(msg.arg1 == DEL_WEIBO){
				Thinksns app = (Thinksns)ThinksnsWeiboContent.this.getApplicationContext();
				Thinksns.setDelIndex(getIntentData().getInt("this_position"));
				ThinksnsWeiboContent.this.finish();
			}
			favorite.setClickable(true);
		}
	}
	
	/*
	 * 处理@、话题、链接点击
	 */
	public ClickableSpan typeClick(final String value) {
		char type = value.charAt(0);
		switch (type) {
		case '@':
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					// TODO Auto-generated method stub
					String uname = "";
					uname = value.substring(1, value.length());
					getIntentData().putInt("uid", 0);
					getIntentData().putString("uname", uname);
					Thinksns app = (Thinksns) ThinksnsWeiboContent.this
							.getApplicationContext();
					app.startActivity(ThinksnsWeiboContent.this,
							ThinksnsUserInfo.class, getIntentData());
				}
				
				@Override
				public void updateDrawState(TextPaint ds) {
					 	ds.setColor(Color.argb(255, 54, 92, 124));
				        ds.setUnderlineText(true);
				}
			};
		case '#':
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					// TODO Auto-generated method stub
					getIntentData().putString("type", "joinTopic");
					getIntentData().putString("topic", value);
					Thinksns app = (Thinksns) ThinksnsWeiboContent.this
							.getApplicationContext();
					/*app.startActivity(ThinksnsWeiboContent.this,
							ThinksnsCreate.class, getIntentData());*/
					app.startActivity(ThinksnsWeiboContent.this,
							ThinksnsTopicActivity.class, getIntentData());
				}
				
				@Override
				public void updateDrawState(TextPaint ds) {
					 	ds.setColor(Color.argb(255, 54, 92, 124));
				        ds.setUnderlineText(true);
				}
			};
		case 'h':
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra("url", value);

					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(value);
					intent.setData(content_url);
					startActivity(intent);

					// intent.setClass(ThinksnsWeiboContent.this,
					// ThinksnsStartWeb.class);
					// ThinksnsWeiboContent.this.startActivity(intent);
				}
				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setColor(Color.argb(255, 54, 92, 124));
					ds.setUnderlineText(true);
				}
			};
		}
		return null;
	}

	/*
	 * 展示大图
	 */
	@Override
	public OnClickListener getImageFullScreen(final String url) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getIntentData().putString("url", url);
				Thinksns app = (Thinksns) ThinksnsWeiboContent.this
						.getApplicationContext();
				app.startActivity(ThinksnsWeiboContent.this,
						ThinksnsImageView.class, getIntentData());
			}

		};
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Log.d("tag", arg1.getX() +"");
		Log.d("tag", arg1.getY() +"");
		Log.d("tag","--- 单独的点击--------------------");
		
		return mGestureDetector.onTouchEvent(arg1);
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		Log.d("tag", e1.getY() +"");
		Log.d("tag", e2.getY() +"");
		Log.d("tag", e2.getYPrecision() +"");
		Log.d("tag","-----------------------");
		
		// TODO Auto-generated method stub
		if (e1.getX() - e2.getX() > 80
				&& Math.abs(velocityX) > 0
				&& Math.abs(e1.getY() - e2.getY()) < 100
				&& Math.abs(velocityX) > 0) {
			// Fling left
			//Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
		} else if (e2.getX() - e1.getX() > 100
				&& Math.abs(velocityX) > 0 ) {
			// Fling right
			Anim.exit(ThinksnsWeiboContent.this);
			ThinksnsWeiboContent.this.finish();
			//Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

}
