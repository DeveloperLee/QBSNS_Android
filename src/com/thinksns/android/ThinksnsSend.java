package com.thinksns.android;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.thinksns.api.ApiMessage;
import com.thinksns.api.ApiStatuses;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LoadingView;
import com.thinksns.components.RightIsButton;
import com.thinksns.components.TSFaceView;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.UpdateException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.model.Comment;
import com.thinksns.model.Weibo;
import com.thinksns.unit.Anim;
import com.thinksns.unit.AtHelper;
import com.thinksns.unit.TSUIUtils;
import com.thinksns.unit.TopicHelper;
import com.thinksns.unit.WordCount;

public class ThinksnsSend extends ThinksnsAbscractActivity {
	
	private static final String TAG = "WeiboSend";
	private static Weibo weibo;
	private static EditText edit;
	private static CheckBox checkBox;
	private static Worker thread;
	private static Handler handler;
	private static LoadingView loadingView;
	private static HashMap<String,Integer> buttonSet;
	private static int replyCommentId=-1;
	
	private ImageView ivTopic;
	private ImageView ivAt;
	private ImageView ivFace;
	
	private TopicHelper mTopicHelper;
	private AtHelper mAtHelper;
	private TSFaceView tFaceView;
	
	private TSFaceView.FaceAdapter mFaceAdapter = new TSFaceView.FaceAdapter(){

		@Override
		public void doAction(int paramInt, String paramString) {
			// TODO Auto-generated method stub
			    EditText localEditBlogView = ThinksnsSend.edit;
			    int i = localEditBlogView.getSelectionStart();
			    int j = localEditBlogView.getSelectionStart();
			    String str1 = "[" + paramString + "]";
			    String str2 = localEditBlogView.getText().toString();
			    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
			    localSpannableStringBuilder.append(str2, 0, i);
			    localSpannableStringBuilder.append(str1);
			    localSpannableStringBuilder.append(str2, j, str2.length());
			    TSUIUtils.highlightContent(ThinksnsSend.this, localSpannableStringBuilder);
			    localEditBlogView.setText(localSpannableStringBuilder, TextView.BufferType.SPANNABLE);
			    localEditBlogView.setSelection(i + str1.length());
			    Log.v("Tag", localEditBlogView.getText().toString());
		}
		
	};
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buttonSet = new HashMap<String,Integer>();
		Thinksns app = (Thinksns)ThinksnsSend.this.getApplicationContext();
		edit = (EditText)findViewById(R.id.send_content);
		checkBox = (CheckBox)findViewById(R.id.isComment);
		loadingView = (LoadingView)findViewById(LoadingView.ID);
		
		ivTopic = (ImageView)findViewById(R.id.topic);
		ivAt = (ImageView)findViewById(R.id.at);
		ivFace = (ImageView)findViewById(R.id.face);
		tFaceView = (TSFaceView) findViewById(R.id.face_view);
		
		mTopicHelper = new TopicHelper(this ,edit);
		mAtHelper = new AtHelper(this , edit);
	    tFaceView.setFaceAdapter(this.mFaceAdapter);

		this.setTextForCheckBox(getIntentData().getInt("send_type"));
		//this.setInputLimit(getIntentData().getInt("send_type"));
		if(this.getIntentData().containsKey("commentId")){
			replyCommentId = this.getIntentData().getInt("commentId");
			edit.setText("回复"+"@"+getIntentData().getString("username")+":  ");
			Editable ea= edit.getText();  //etEdit为EditText 
			Selection.setSelection(ea, ea.length()); 
		}else{
			replyCommentId = -1;
		}
		if(getIntentData().getInt("send_type") != REPLY_MESSAGE && getIntentData().getInt("send_type")!=CREATE_MESSAGE){
			try {
				weibo = new Weibo(new JSONObject(getIntentData().getString("data")));
			} catch (WeiboDataInvalidException e) {
				ThinksnsSend.this.finish();
			} catch (JSONException e) {
				ThinksnsSend.this.finish();
			}	
		}
		
		this.setInputLimit(getIntentData().getInt("send_type"));
		
		setBottomClick();
		
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
	
	private void setInputLimit(int type) {
		TextView overWordCount = (TextView)findViewById(R.id.overWordCount);

		if(type != REPLY_MESSAGE || type != CREATE_MESSAGE){
			if(type == TRANSPOND){
				String tran = "";
				if(weibo.getTranspond() != null){
					tran = "//@"+weibo.getUsername()+":" + weibo.getContent();
				}
				WordCount wordCount = new WordCount(edit,overWordCount ,tran);
				edit.addTextChangedListener(wordCount);
			}else{
				WordCount wordCount = new WordCount(edit,overWordCount);
				overWordCount.setText(wordCount.getMaxCount()+"");
				edit.addTextChangedListener(wordCount);
			}
		}else{
			overWordCount.setVisibility(View.GONE);
		}
		/*if(type != REPLY_MESSAGE || type != CREATE_MESSAGE){
			WordCount wordCount = new WordCount(edit,overWordCount);
			overWordCount.setText(wordCount.getMaxCount()+"");
			edit.addTextChangedListener(wordCount);
		}else{
			overWordCount.setVisibility(View.GONE);
		}*/
	}

	private void setTextForCheckBox(int type) {
		switch(type){
		case TRANSPOND:
			checkBox.setText(R.string.transpond_checkbox);
			break;
		case COMMENT:
			checkBox.setText(R.string.comment_checkbox);
			break;
		case REPLY_MESSAGE:
			checkBox.setVisibility(View.GONE);
			ivAt.setVisibility(View.GONE);
			ivTopic.setVisibility(View.GONE);
		case CREATE_MESSAGE:
			checkBox.setVisibility(View.GONE);	
			ivAt.setVisibility(View.GONE);
			ivTopic.setVisibility(View.GONE);
			break;
		}
	}
	
	/**
	 * 各种按钮事件处理
	 */
	private void setBottomClick() {
		
		ivTopic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTopicHelper.insertTopicTips();
			}
		});
		
		ivAt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAtHelper.insertAtTips();
			}
		});
		
		ivFace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tFaceView.getVisibility() == View.GONE){
					tFaceView.setVisibility(View.VISIBLE);
					ivFace.setImageResource(R.drawable.btn_insert_keyboard);
					TSUIUtils.hideSoftKeyboard(ThinksnsSend.this, edit);
				}else if(tFaceView.getVisibility() == View.VISIBLE){
					tFaceView.setVisibility(View.GONE);
					ivFace.setImageResource(R.drawable.btn_insert_face);
					TSUIUtils.showSoftKeyborad(ThinksnsSend.this, edit);
				}
			}
		});
		
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tFaceView.getVisibility() == View.VISIBLE){
					tFaceView.setVisibility(View.GONE);
					ivFace.setImageResource(R.drawable.btn_insert_face);
					TSUIUtils.showSoftKeyborad(ThinksnsSend.this, edit);
				System.out.println(" show soft key board ...");}
			}
		});
	}
	
	
	private  final class ActivityHandler extends Handler {
		private  Context context = null;


		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			loadingView.show(edit);
			
			//获取数据
			Thinksns app = thread.getApp();
			ApiStatuses statuses = app.getStatuses();
			ApiMessage message = app.getMessages();

			try {
				switch(msg.what){
				case ThinksnsAbscractActivity.COMMENT:
					if(edit.getText().length() == 0){
						loadingView.error("评论不能为空",edit);
						loadingView.hide(edit);
					}else{
						String editContent =  edit.getText().toString();
						
						
						Comment comment = new Comment();
						comment.setContent(editContent);
						comment.setStatus(weibo);
						comment.setType(checkBox.isChecked()?Comment.Type.WEIBO:Comment.Type.COMMENT);
						
						if(replyCommentId != -1){
							Comment recomment = new Comment();
							recomment.setCommentId(replyCommentId);
							comment.setReplyComment(recomment);
						}
						
						if(statuses.comment(comment)){
							//Toast.makeText(ThinksnsSend.this, "评论成功", Toast.LENGTH_SHORT).show(); 
							loadingView.error("评论成功", edit);
							sendFlag = true;
							ThinksnsSend.this.finish();
							//app.getActivityStack().returnActivity(ThinksnsSend.this,getIntentData());
						}else{
							clearSendingButtonAnim(getCustomTitle().getRight());
							loadingView.error("评论失败",edit);
						}
					}
					break;
				case ThinksnsAbscractActivity.TRANSPOND:
					String editContent = edit.getText().length() > 0 ? edit.getText().toString():"转发微博";
					
					Weibo newWeibo = new Weibo();
					newWeibo.setContent(editContent);
					//newWeibo.setTranspond(weibo);
					newWeibo.setTranspond((weibo.getTranspond())==null ? weibo:(weibo.getTranspond()));
					statuses.repost(newWeibo, checkBox.isChecked());
					Toast.makeText(ThinksnsSend.this, "转发成功", Toast.LENGTH_SHORT).show(); 
					//getIntentData().putString(TIPS, "转发微博成功");
					ThinksnsSend.this.finish();
					//app.getActivityStack().returnActivity(ThinksnsSend.this,getIntentData());
					break;
				case ThinksnsAbscractActivity.REPLY_MESSAGE:
					com.thinksns.model.Message messageObj = new com.thinksns.model.Message();
					com.thinksns.model.Message replyMessage = new com.thinksns.model.Message();
					replyMessage.setListId(getIntentData().getInt("messageId"));
					replyMessage.setContent(edit.getText().toString());
				//	messageObj.setSourceMessage(replyMessage);
					
					if(message.reply(replyMessage)){
						getIntentData().putString(TIPS, "回复消息成功");
						sendFlag = true;
					//	Toast.makeText(ThinksnsSend.this, "回复消息成功", Toast.LENGTH_SHORT).show(); 
						ThinksnsSend.this.finish();
						//app.getActivityStack().returnActivity(ThinksnsSend.this,getIntentData());
					}else{
						clearSendingButtonAnim(getCustomTitle().getRight());
						loadingView.error("回复消息失败",edit);
					}
					break;
				case ThinksnsAbscractActivity.CREATE_MESSAGE:
					com.thinksns.model.Message createMessage  = new com.thinksns.model.Message();
					createMessage.setToUid(getIntentData().getInt("to_uid"));
					createMessage.setContent(edit.getText().toString());
					createMessage.setTitle("new message");
					Log.e("uid","getIntentData"+getIntentData().getInt("to_uid"));
					Log.e("content","content"+edit.getText().toString());
					if(message.createNew(createMessage)){
						//Toast.makeText(ThinksnsSend.this, "发送成功", Toast.LENGTH_SHORT).show(); 
						getIntentData().putString(TIPS, "发送成功");
						ThinksnsSend.this.finish();
						//app.getActivityStack().returnActivity(ThinksnsSend.this,getIntentData());
					}else{
						clearSendingButtonAnim(getCustomTitle().getRight());
						loadingView.error("发送失败",edit);
					}
					break;
				}
				
			} catch (VerifyErrorException e) {
				clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.error(e.getMessage(),edit);
			} catch (ApiException e) {
				clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.error(e.getMessage(),edit);
			} catch (UpdateException e) {
				clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.error(e.getMessage(),edit);
			} catch (DataInvalidException e) {
				clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.error(e.getMessage(),edit);
			}
			thread.quit();
		}
	}
	
	private void sendingButtonAnim(View v){
		buttonSet.put("left", v.getPaddingLeft());
		buttonSet.put("right", v.getPaddingRight());
		buttonSet.put("buttom", v.getPaddingBottom());
		buttonSet.put("top", v.getPaddingTop());
		buttonSet.put("height", v.getHeight());
		buttonSet.put("width", v.getWidth());
		buttonSet.put("text", R.string.publish);
		
		TextView view = (TextView)v;
		view.setHeight(24);
		view.setWidth(24);
		view.setText("");
		Anim.refresh(this, view);
	}
	
	private void clearSendingButtonAnim(View v){
		TextView view = (TextView)v;
		view.setHeight(buttonSet.get("height"));
		view.setWidth(buttonSet.get("width"));
		view.setText(this.getString(buttonSet.get("text")));
		view.clearAnimation();
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.transpond;
	}


	@Override
	public OnClickListener getRightListener() {
		return  new OnClickListener() {
			@Override
			public void onClick(View v) {
				//sendingButtonAnim(v);
				if(edit.getText().toString().length() >140){
					//loadingView.error(getString(R.string.word_limit),edit);
					Toast.makeText(getApplicationContext(), R.string.word_limit, Toast.LENGTH_SHORT).show();
					
				}else {
				Thinksns app = (Thinksns)ThinksnsSend.this.getApplicationContext();
				thread = new Worker(app,"Publish data");
				handler = new ActivityHandler(thread.getLooper(),ThinksnsSend.this);
				Message msg = handler.obtainMessage(getIntentData().getInt("send_type"));
				handler.sendMessage(msg);}

			}
		};
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new RightIsButton(this,this.getString(R.string.sendMessage));
	}
	
	
	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return R.drawable.button_send;
	}

	@Override
	public String getTitleCenter() {
		switch(getIntentData().getInt("send_type")){
		case TRANSPOND:
			return this.getString(R.string.transpond);
		case COMMENT:
			return this.getString(R.string.comment);
		case REPLY_MESSAGE:
			return this.getString(R.string.private_letter);
		case CREATE_MESSAGE:
			return this.getString(R.string.private_letter);
		}
		return  null;
	}
}
