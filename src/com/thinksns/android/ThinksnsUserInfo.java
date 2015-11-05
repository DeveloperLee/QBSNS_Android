package com.thinksns.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.FollowListAdapter;
import com.thinksns.api.Api;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.components.NumberButton;
import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.User;
import com.thinksns.model.Weibo;
import com.thinksns.unit.Anim;
import com.thinksns.unit.TSUIUtils;
  


public class ThinksnsUserInfo extends ThinksnsAbscractActivity {
	private static NumberButton followers;
	private static NumberButton followeds;
	private static NumberButton weibos;
	private static TextView username;
	private static TextView location;
	private static Button followButton;
	private static final int DEFAULT = 0;
	private static final String DEFAULT_NULL = " ";
	private static final int ADD_FOLLOWED = 0;
	private static final int DEL_FOLLOWED = 1;
	private static final int LOAD_USER_INFO = 2;
	private static final int ADD_BLACKLIST = 3;
	private static final int DEL_BLACKLIST = 4;
	private static final String TAG = "UserInfo";
	private static ResultHandler resultHandler;
	private static ImageView header;
	private static ImageButton blackList;
	private User userCache;
	private static ActivityHandler handler;
	private static TextView userWeibo;
	private static boolean refreshing = false;
	private static Drawable oldDrawable;
	public static enum FollowedStatus{
		YES,NO
	}
	public static enum BlackListStatus{
		YES,NO
	}
	private static String temp;
	
	private static int uid = 0;
	private static String uname = null;
	private static LinearLayout linearLayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(this.isInTab()){
			super.onCreateDefault(savedInstanceState);
		}else{
			super.onCreate(savedInstanceState);
		}
		followers = (NumberButton)findViewById(R.id.followers);
		followeds = (NumberButton)findViewById(R.id.followeds);
		weibos = (NumberButton)findViewById(R.id.weibos);
		username = (TextView)findViewById(R.id.user_name);
		followButton = (Button)findViewById(R.id.button_follow);
		location = (TextView)findViewById(R.id.user_location);
		header =(ImageView)findViewById(R.id.userInfo_header);
		linearLayout = (LinearLayout)findViewById(R.id.userinfo_util);
		blackList =(ImageButton)findViewById(R.id.black_list);
		followButton.setClickable(false);
		weibos.setClickable(false);
		followers.setClickable(false);
		followeds.setClickable(false);
		userWeibo = (TextView) findViewById(R.id.firstweibo);
		setLayoutText();
		Thinksns app = (Thinksns)this.getApplicationContext();
		uid = Thinksns.getMy().getUid();
		uname = Thinksns.getMy().getUserName();
		//app.getUserSql().clear();
		//User temp = new User();
		try {
			userCache = app.getUserSql().getUser("uid="+uid);
		} catch (UserDataInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(getIntentData().containsKey("uname")&&getIntentData().getString("uname")!= uname){
			threadLoadingData();
		}else if(getIntentData().containsKey("uid")&&getIntentData().getInt("uid")!=uid ){
			threadLoadingData();
		}else if(userCache == null){
			threadLoadingData();
		}else{
			this.setUserInfoView(userCache);
		}
		setOnClick();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if(this.isInTab())super.initTitle();
		if(sendFlag){
			threadLoadingData();
			sendFlag = false;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	private void setUserInfoView(User user){
		resultHandler = new ResultHandler();
		ThinksnsUserInfo.this.setUserInfoButton();
		followeds.setCount(user.getFollowersCount());
		followers.setCount(user.getFollowedCount());
		weibos.setCount(user.getWeiboCount());
		username.setText(user.getUserName());
		followButton.setVisibility(View.GONE);
		//location.setText(user.getLocation());
		if(user.getIsInBlackList()){
			blackList.setBackgroundResource(R.drawable.cacle_black);
		}
		if(user.getLocation().equals("null")){
			location.setVisibility(View.GONE);
		}else{
			location.setText(user.getLocation());
		}
		if(user.getLastWeibo() != null){
			String patternStr = user.getLastWeibo().getContent();
			try{
				Pattern pattern =Pattern.compile("((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)") ;
				Matcher matcher = pattern.matcher(patternStr);
				SpannableString ss = new SpannableString(patternStr);
				while(matcher.find()){              
					ss.setSpan(new ForegroundColorSpan(Color.argb(255, 54, 92, 124)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				TSUIUtils.highlightContent(ThinksnsUserInfo.this, ss);
				userWeibo.setText(ss);
			}catch(Exception ex){
				userWeibo.setText(patternStr);
			}
			//userWeibo.setText(user.getLastWeibo().getContent());
			userWeibo.setTag(user.getLastWeibo());
		}else{
			userWeibo.setVisibility(View.GONE);
		}
		resultHandler.loadHeader(user); 
		buttonClickable(true);
	}

	private void buttonClickable(boolean clickable) {
		refreshing = !clickable;

		if (this.isInTab()) {
			if (refreshing) {
				//设置动画
				Anim.refresh(this, this.getCustomTitle().getRight(),R.drawable.spinner_black_60);
				oldDrawable = this.getCustomTitle().getRight().getBackground();
			} else {
				//取消动画
				this.getCustomTitle().getRight().clearAnimation();
				this.getCustomTitle().getRight()
						.setBackgroundResource(R.drawable.button_refresh);
			}
		}

		if (clickable) {
			Thinksns app = (Thinksns) ThinksnsUserInfo.this
					.getApplicationContext();

			if ((getIntentData().containsKey("uid")
					&& getIntentData().getInt("uid") != Thinksns.getMy().getUid() && getIntentData()
					.getInt("uid") != 0)
					|| (getIntentData().containsKey("uname")
							&& ! getIntentData().getString("uname").equals(Thinksns
									.getMy().getUserName())&& getIntentData()
							.getString("uname") != null)) {
				followButton.setVisibility(View.VISIBLE);
				linearLayout.setVisibility(View.VISIBLE);
			}
			
		} else {
			followButton.setVisibility(View.GONE);
		}
		followButton.setClickable(clickable);
		weibos.setClickable(clickable);
		followers.setClickable(clickable);
		followeds.setClickable(clickable);
		if (userWeibo.getVisibility() == View.VISIBLE) {
			userWeibo.setClickable(clickable);
		}
	}

	private void setOnClick() {
		//关注
		followers.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getIntentData().putInt("type", FollowListAdapter.FOLLOWER);
				getIntentData().putString("data", userCache.toJSON());
				if(userCache.getUserJson()!=null){
					getIntentData().putString("data", userCache.getUserJson());
				}else{
					Log.e("user","user"+userCache.toJSON());
					getIntentData().putString("data", userCache.toJSON());
				}
				
				Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
				app.startActivity(getTabActivity(),ThinksnsFollow.class,getIntentData());
			}
		});
		//粉丝,从数据库中获得用户信息
		followeds.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getIntentData().putInt("type", FollowListAdapter.FOLLOWED);
				if(userCache.getUserJson()!=null){
					getIntentData().putString("data", userCache.getUserJson());
				}else{
					getIntentData().putString("data", userCache.toJSON());
				}
				Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
				app.startActivity(getTabActivity(),ThinksnsFollow.class,getIntentData());
			}
			
		});
		
		followButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				v.setClickable(false);
				Message msg = handler.obtainMessage();
				if((ThinksnsUserInfo.FollowedStatus)v.getTag() == ThinksnsUserInfo.FollowedStatus.YES){
					msg.what = DEL_FOLLOWED;
				}else{
					msg.what = ADD_FOLLOWED;
				}
				User user = new User();
				user.setUid(userCache.getUid());
				msg.obj = user;
				handler.sendMessage(msg);
			}
		});
		
		userWeibo.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v){
				Weibo weibo = (Weibo)v.getTag();
				getIntentData().putString("data", weibo.toJSON());
				getIntentData().putBoolean("tab", false);
				Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
				app.startActivity(getTabActivity(),ThinksnsWeiboContent.class,getIntentData());
			}
		});
		
		weibos.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getIntentData().putInt("uid", userCache.getUid());
				getIntentData().putString("uname", userCache.getUserName());
				Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
				app.startActivity(getTabActivity(),ThinksnsUserWeibo.class,getIntentData());
			}
			
		});
	}
	
	/**
	 * 刷新个人资料
	 */
	@Override
	public OnClickListener getRightListener() {
		if(this.isInTab()){
			return new OnClickListener(){
				@Override
				public void onClick(View v) {
					threadLoadingData();
				}
			};
		}else{
			return super.getRightListener();
		}
	}

	

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		if(this.isInTab()){
			return R.drawable.button_refresh;
		}else{
			return super.getRightRes();
		}
	}

	@Override
	public int getLeftRes() {
		// TODO Auto-generated method stub
		if(this.isInTab()){
			return R.drawable.button_new;
		}else{
			return super.getLeftRes();
		}
	}

	@Override
	public OnClickListener getLeftListener() {
		// TODO Auto-generated method stub
		if(this.isInTab()){
			return new OnClickListener(){
				@Override
				public void onClick(View v) {
					Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
					app.startActivity(getTabActivity(),ThinksnsCreate.class,null);
				}
			};
		}else{
			return super.getLeftListener();
		}
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.userinfo;
	}


	@Override
	protected CustomTitle setCustomTitle() {
		return 	new LeftAndRightTitle(this);
	}

	@Override
	public boolean isInTab() {
		return this.getIntent().getBooleanExtra("tab", false);
	}

	@Override
	public String getTitleCenter() {
		return this.getString(R.string.info);
	}
	

	private void setLayoutText() {
		followers.setText(R.string.follow);
		followeds.setText(R.string.followed);
		weibos.setText(R.string.weibo);
		 
		followers.setCount(DEFAULT);
		followeds.setCount(DEFAULT);
		weibos.setCount(DEFAULT);
		
		username.setText(DEFAULT_NULL);
		location.setText(DEFAULT_NULL);
		
	}

	private void threadLoadingData() {
		if(refreshing){
			Toast.makeText(this, "正在刷新中，请勿重新点击",Toast.LENGTH_LONG) .show(); 
			return;
		}
		
		buttonClickable(false);
		resultHandler = new ResultHandler();
		Thinksns app =(Thinksns)this.getApplicationContext();
		Worker thread = new Worker(app,"Loading UserInfo");
		handler = new ActivityHandler(thread.getLooper(),this);
		Message msg = handler.obtainMessage();
		msg.what = LOAD_USER_INFO;
		User user = new User();
		user.setUid(getIntentData().containsKey("uid")?getIntentData().getInt("uid"):Thinksns.getMy().getUid());
		if(getIntentData().getString("uname") != null){
			user.setUserName(getIntentData().containsKey("uname")?getIntentData().getString("uname"):Thinksns.getMy().getUserName());
		}

		msg.obj = user;
		handler.sendMessage(msg);
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
			Api.Friendships friendships= app.getFriendships();
			Api.Users userApi = app.getUsers();
			try {
				switch(msg.what){
				case ADD_FOLLOWED:
					newData = friendships.create((User)msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj  = newData;
					mainMsg.arg1 = msg.what;
					break;
				case DEL_FOLLOWED:
					newData = friendships.destroy((User)msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj  = newData;
					mainMsg.arg1 = msg.what;
					break;
				case LOAD_USER_INFO:
					User user  = userApi.show((User)msg.obj);
					if(user.getUid() == Thinksns.getMy().getUid()){
						//添加认证信息
						user.setToken(Thinksns.getMy().getToken());
						user.setSecretToken(Thinksns.getMy().getSecretToken());
						app.getUserSql().updateUser(user);
					}
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj  = user;
					mainMsg.arg1 = msg.what;
					break;
					
				case ADD_BLACKLIST:
					newData = friendships.addBlackList((User)msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj  = newData;
					mainMsg.arg1 = msg.what;
					break;
				case DEL_BLACKLIST:
					newData = friendships.delBlackList((User)msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj  = newData;
					mainMsg.arg1 = msg.what;
					break;
				}
			} catch (VerifyErrorException e) {
				mainMsg.obj = e.getMessage();
				refreshing=false;
				Log.e(TAG, e.getMessage());
			} catch (ApiException e) {
				mainMsg.obj = e.getMessage();
				refreshing=false;
				Log.e(TAG, e.getMessage());
			} catch (DataInvalidException e) {
				mainMsg.obj = e.getMessage();
				refreshing=false;
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
				switch (msg.arg1){
				case ADD_FOLLOWED:
					followButton.setTag(ThinksnsUserInfo.FollowedStatus.YES);
					followButton.setBackgroundResource(R.drawable.button_is_follow);
					info = "关注成功";
					Toast.makeText(ThinksnsUserInfo.this,info, Toast.LENGTH_SHORT).show();
					followButton.setClickable(true);
					break;
				case DEL_FOLLOWED:
					followButton.setTag(ThinksnsUserInfo.FollowedStatus.NO);
					followButton.setBackgroundResource(R.drawable.button_follow);
					info = "取消关注成功";
					Toast.makeText(ThinksnsUserInfo.this,info, Toast.LENGTH_SHORT).show();
					followButton.setClickable(true);
					break;
				case LOAD_USER_INFO:	
					User user = (User)msg.obj;
					userCache = user;
					ThinksnsUserInfo.this.setUserInfoButton();
					followeds.setCount(user.getFollowersCount());
					followers.setCount(user.getFollowedCount());
					weibos.setCount(user.getWeiboCount());
					username.setText(user.getUserName());
					//location.setText(user.getLocation());
					if(user.getLocation().equals("null")){
						location.setVisibility(View.GONE);
					}else{
						location.setText(user.getLocation());
					}
					if(user.getLastWeibo() != null){
						userWeibo.setText(user.getLastWeibo().getContent());
						userWeibo.setTag(user.getLastWeibo());
					}else{
						userWeibo.setVisibility(View.GONE);
					}
					if(user.isFollowed()){
						followButton.setTag(ThinksnsUserInfo.FollowedStatus.YES);
						followButton.setBackgroundResource(R.drawable.button_is_follow);
					}else{
						followButton.setTag(ThinksnsUserInfo.FollowedStatus.NO);
						followButton.setBackgroundResource(R.drawable.button_follow);
					}
					this.loadHeader(user); 
					buttonClickable(true);
					if(user.getIsInBlackList()){
						followButton.setVisibility(View.GONE);
					}
					break;
				case ADD_BLACKLIST:
					blackList.setTag(ThinksnsUserInfo.BlackListStatus.YES);
					blackList.setImageResource(R.drawable.cacle_black);
					info = "已加入黑名单";
					Toast.makeText(ThinksnsUserInfo.this,info, Toast.LENGTH_SHORT).show();
					followButton.setVisibility(View.GONE);
					break;
				case DEL_BLACKLIST:
					blackList.setTag(ThinksnsUserInfo.BlackListStatus.NO);
					blackList.setImageResource(R.drawable.userinfo_blacklist);
					info = "已从黑名单取消";
					Toast.makeText(ThinksnsUserInfo.this,info, Toast.LENGTH_SHORT).show();
					followButton.setBackgroundResource(R.drawable.button_follow);
					followButton.setVisibility(View.VISIBLE);
					break;
					
				}
			}else{
				info = (String)msg.obj;
				Toast.makeText(ThinksnsUserInfo.this,info, Toast.LENGTH_SHORT).show();
				followButton.setClickable(false);
			}
			
		}

		
		public void loadHeader(User user) {
			header.setTag(user);
			Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
			if(user.hasHeader()&&app.isNetWorkOn()){
				if(user.isNullForHeaderCache()){
					dowloaderTask(user.getUserface(), header,BitmapDownloaderTask.Type.FACE);
				}else{
					Bitmap cache = user.getHeader();
					if(cache == null){
						dowloaderTask(user.getUserface(), header,BitmapDownloaderTask.Type.FACE);
					}else{
						header.setImageBitmap(cache);
					}
				}
			}
		}
		final protected  void dowloaderTask(String url,ImageView image,BitmapDownloaderTask.Type type) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(image,type);
			task.execute(url);
		}
	}

	protected Activity getTabActivity(){
		if(this.isInTab()){
			ThinksnsHome home = (ThinksnsHome) this.getParent();
			getIntentData().putInt("tabId", home.getCurrentTabId());
			return home;
		}else{
			return this;
		}
	}
	
	private void setUserInfoButton(){
		ImageView refreash = (ImageView)findViewById(R.id.refreash);
		refreash.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ThinksnsUserInfo.this.threadLoadingData();
			}
			
		});
		
		ImageView atHe = (ImageView)findViewById(R.id.at_he);
		atHe.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String at = "@"+userCache.getUserName()+" ";
				getIntentData().putString("type", "joinTopic");
				getIntentData().putString("topic", at);
				Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
				app.startActivity(ThinksnsUserInfo.this,ThinksnsCreate.class,getIntentData());
			}
			
		});
		
		ImageView sendMessage = (ImageView)findViewById(R.id.send_message);
		sendMessage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getIntentData().putInt("to_uid", userCache.getUid());
				getIntentData().putInt("send_type", ThinksnsAbscractActivity.CREATE_MESSAGE);
				Thinksns app = (Thinksns)ThinksnsUserInfo.this.getApplicationContext();
				app.startActivity(ThinksnsUserInfo.this, ThinksnsSend.class,getIntentData());
			}
			
		});
		blackList.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Message msg = handler.obtainMessage();
				if((ThinksnsUserInfo.BlackListStatus)v.getTag() == ThinksnsUserInfo.BlackListStatus.YES){
					msg.what = DEL_BLACKLIST;
				}else{
					msg.what = ADD_BLACKLIST;
				}
				msg.obj = userCache;
				handler.sendMessage(msg);				
			}
		});
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//if(isInTab()){
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			 dialog(); 
	         return false; 
		//}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}





