package com.thinksns.android;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.thinksns.adapter.SearchWeiboListAdapter;
import com.thinksns.adapter.WeiboListAdapter;
import com.thinksns.api.Api;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LeftAndRightTitle;
import com.thinksns.components.WeiboList;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;

public class ThinksnsTopicActivity extends ThinksnsAbscractActivity {

	private static final String TAG = "ThinksnsTopicActivity";

	private static WeiboList topicList;
	private static WeiboListAdapter topicAdapter;

	private ImageButton followTopic;
	private ImageButton sendTopic;
//	private Object context;

	private String topicStr;
	private String topic;

	private ActivityHandler activityHandler;
	private ResultHandler resultHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		topicList = (WeiboList) findViewById(R.id.weiboList_home);
		followTopic = (ImageButton) findViewById(R.id.follow_topic);
		sendTopic = (ImageButton) findViewById(R.id.send_topic);
		// 得到话题
		topic = getIntentData().getString("topic");
		// 不带#号的话题
		topicStr = topic.substring(1, topic.length() - 1);
		ListData<SociaxItem> data = new ListData<SociaxItem>();
		// 收索话题
		topicAdapter = new SearchWeiboListAdapter(ThinksnsTopicActivity.this,
				data, topic);
		topicList.setAdapter(topicAdapter, System.currentTimeMillis(),
				ThinksnsTopicActivity.this);
		topicAdapter.loadInitData();
	
		// 设置按钮监听事件
		setOnClickListener();
		int position = 0;
		Bundle temp = null;
		if ((temp = getIntent().getExtras()) != null) {
			position = temp.getInt("position");
		}
		topicList.setSelectionFromTop(position, 1);		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		// 初始化
		threadLoadingData();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 设置按钮监听事件
	 */
	private void setOnClickListener() {
		followTopic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = activityHandler.obtainMessage();
				if ((ThinksnsUserInfo.FollowedStatus) (followTopic.getTag()) == ThinksnsUserInfo.FollowedStatus.YES) {
					msg.what = UNFOLLOWTOPIC;
				} else if ((ThinksnsUserInfo.FollowedStatus) followTopic
						.getTag() == ThinksnsUserInfo.FollowedStatus.NO) {
					msg.what = FOLLOWTOPIC;
				}
				msg.obj = topicStr;
				// 发送消息
				activityHandler.sendMessage(msg);
				System.out.println("activityHandler.sendMessage(msg)"+msg.what);
				/*
				 * if (followTopicResult) { result = "关注成功"; } else { result =
				 * "关注失败"; } Toast.makeText(ThinksnsTopicActivity.this, result,
				 * 500).show();
				 */
			}
		});

		sendTopic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thinksns app = (Thinksns) ThinksnsTopicActivity.this
						.getApplicationContext();
				app.startActivity(ThinksnsTopicActivity.this,
						ThinksnsCreate.class, getIntentData());
			}
		});

	}

	/** isFollowedTopic */
	private static final int ISFOLLOWEDTOPIC = 1;
	/** followTopic */
	private static final int FOLLOWTOPIC = 2;
	/** unFollowTopic */
	private static final int UNFOLLOWTOPIC = 3;

	private void threadLoadingData() {
		followTopic.setClickable(false);
		followTopic.setTag(ThinksnsUserInfo.FollowedStatus.NO);
		Thinksns app = (Thinksns) this.getApplicationContext();
		Worker thread = new Worker(app, "Loading followTopic");
		//thread.sleep(1000);
		activityHandler = new ActivityHandler(thread.getLooper(), this);
		resultHandler = new ResultHandler();
		Message msg = activityHandler.obtainMessage();
		msg.what = ISFOLLOWEDTOPIC;
		msg.obj = topicStr;
		// 发送消息
		//activityHandler.sendMessage(msg);
		sysLoadingData();
	}

	private class ActivityHandler extends Handler {
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			boolean stateResult = false;
			Message mainMsg = new Message();
			mainMsg.what = ResultHandler.ERROR;
			Thinksns app = (Thinksns) this.context.getApplicationContext();
			Api.Friendships friendships = app.getFriendships();
			try {
				switch (msg.what) {
				case ISFOLLOWEDTOPIC:
					stateResult = friendships.isFollowTopic(null, topicStr);
					System.out.println("话题状态" + stateResult);
					mainMsg.what = ResultHandler.SUCCESS;
					/** 关注状态结果 */
					mainMsg.obj = stateResult;
					mainMsg.arg1 = msg.what;
					break;
				case FOLLOWTOPIC:
					stateResult = friendships.followTopic(null, topicStr);
					mainMsg.what = ResultHandler.SUCCESS;

					mainMsg.obj = stateResult;
					mainMsg.arg1 = msg.what;
					break;
				case UNFOLLOWTOPIC:
					stateResult = friendships.unFollowTopic(null, topicStr);
					mainMsg.what = ResultHandler.SUCCESS;

					mainMsg.obj = stateResult;
					mainMsg.arg1 = msg.what;
					break;
				}
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

	private class ResultHandler extends Handler {
		private static final int SUCCESS = 0;
		private static final int ERROR = 1;

		@Override
		public void handleMessage(Message msg) {
			String info = "";
			if (msg.what == SUCCESS) {
				switch (msg.arg1) {
				case ISFOLLOWEDTOPIC:
					if ((Boolean) (msg.obj)) {
						followTopic.setTag(ThinksnsUserInfo.FollowedStatus.YES);
						//followTopic
								//.setBackgroundResource(R.drawable.followed_topic);
						followTopic.setImageResource(R.drawable.followed_topic);
					} else {
						followTopic.setTag(ThinksnsUserInfo.FollowedStatus.NO);
						/*followTopic
								.setBackgroundResource(R.drawable.follow_topic);*/
						followTopic.setImageResource(R.drawable.follow_topic);
					}
					followTopic.setClickable(true);
					System.out.println("handleMesage"+msg.arg1+"-------- "+ msg.obj);
					break;
				case FOLLOWTOPIC:
					if ((Boolean) (msg.obj)) {
						followTopic.setTag(ThinksnsUserInfo.FollowedStatus.YES);
						/*followTopic
								.setBackgroundResource(R.drawable.followed_topic);*/
						followTopic.setImageResource(R.drawable.followed_topic);
						info="关注话题成功";
						Toast.makeText(ThinksnsTopicActivity.this, info,Toast.LENGTH_SHORT).show();
					} else {
					}
					break;
				case UNFOLLOWTOPIC:
					if ((Boolean) (msg.obj)) {
						followTopic.setTag(ThinksnsUserInfo.FollowedStatus.NO);
						/*followTopic
								.setBackgroundResource(R.drawable.follow_topic);*/
						followTopic.setImageResource(R.drawable.follow_topic);
						info="取消关注成功";
						Toast.makeText(ThinksnsTopicActivity.this, info,Toast.LENGTH_SHORT).show();
					} else {
					}
					break;
				}
			}
		}
	}

	private void sysLoadingData() {
		Thinksns app = (Thinksns) getApplicationContext();
		Api.Friendships friendships = app.getFriendships();
		boolean stateResult = false;
		try {
			stateResult = friendships.isFollowTopic(null, topicStr);
		} catch (VerifyErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (stateResult) {
			followTopic.setTag(ThinksnsUserInfo.FollowedStatus.YES);
			/*followTopic
					.setBackgroundResource(R.drawable.followed_topic);*/
			followTopic.setImageResource(R.drawable.followed_topic);
		} else {
			followTopic.setTag(ThinksnsUserInfo.FollowedStatus.NO);
			/*followTopic
					.setBackgroundResource(R.drawable.follow_topic);*/
			followTopic.setImageResource(R.drawable.follow_topic);
		}
		followTopic.setClickable(true);
	}

	// /////////继承////////////
	@Override
	public void refreshHeader() {
		topicAdapter.isRefreshActivity = "ThinksnsTopicActivity";
		topicAdapter.doRefreshHeader();
	}

	@Override
	public void refreshFooter() {
		topicAdapter.doRefreshFooter();
	}

	@Override
	public OnTouchListListener getListView() {
		return topicList;
	}

	// 头部右边刷新
	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				ThinksnsTopicActivity.this.refreshHeader();
			}

		};
	}

	// 继承absActivity
	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return "话题";
	}

	// 继承absActivity
	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return new LeftAndRightTitle(this);
	}

	@Override
	public int getRightRes() {
		return R.drawable.button_refresh;
	}

	// 继承absActivity
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.topic;
	}
}
