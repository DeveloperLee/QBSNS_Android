package com.thinksns.components;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.android.ThinksnsSend;
import com.thinksns.android.ThinksnsUserInfo;
import com.thinksns.android.ThinksnsWeiboContentList;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.model.Comment;
import com.thinksns.model.Weibo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class ContentCommentList extends SociaxList{
	private static View v;
	private static final int SHOW_USER =0;
	private static final int REPLY_COMMENT =1;
	private static final int DEL_COMMENT = 2;
	private static Comment comment;
	private static String weibo;
	private static  ThinksnsAbscractActivity context;
	
	
	private int commentPosition = -1;
	private View headView;
	
	public View getHeadView() {
		return headView;
	}

	public void setHeadView(View headView) {
		this.headView = headView;
	}

	public ContentCommentList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContentCommentList(ThinksnsAbscractActivity context) {
		super(context);
		ContentCommentList.context = context;
	}
//点击单条评论，进入发表评论
	@Override
	protected void onClick(View view,int position,long id) {
		Thinksns app = (Thinksns)getContext().getApplicationContext();
	//	Bundle data = new Bundle();
		//data.putInt("send_type", ThinksnsWeiboComment.COMMENT);
		//Comment comment = (Comment)view.findViewById(R.id.comment_content).getTag();
		weibo = (String)view.getTag();
		Weibo wb = null;
		try {
			wb = new Weibo(new JSONObject(weibo));
		} catch (WeiboDataInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlertDialog.Builder builder = new Builder(view.getContext());
		CommentListener listener = new CommentListener();
		 comment = (Comment)this.getItemAtPosition(position);
		 comment.setPosition(position);
		 commentPosition = position;
		 if(Thinksns.getMy().getUid() != comment.getUid() ){
			 builder.setItems(R.array.del_topts, listener).setTitle("评论功能").setCancelable(true).show();
		 }else{
			 builder.setItems(R.array.del_commentopts, listener).setTitle("评论功能").setCancelable(true).show();
		 }
		
		
	}
	
	private class CommentListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Thinksns app = (Thinksns)getContext().getApplicationContext();
			Bundle data = new Bundle();
			switch (which){
			case SHOW_USER:
				data.putInt("uid", comment.getUid());
				app.startActivity(getActivityObj(),ThinksnsUserInfo.class,data);
				break;
			case REPLY_COMMENT:
				data.putInt("send_type", ThinksnsAbscractActivity.COMMENT);
				data.putInt("commentId", comment.getCommentId());
				data.putString("username", comment.getUname());
				data.putString("data",weibo);
				app.startActivity(getActivityObj(),ThinksnsSend.class,data);
				break;
			case DEL_COMMENT:
				((ThinksnsWeiboContentList)getActivityObj()).deleteComment(comment);
				break;
			}
		}
	}
	
	
	
	@Override
	protected void addHeaderView() {
		
		this.addHeaderView(getHeadView());
	}
	
}
