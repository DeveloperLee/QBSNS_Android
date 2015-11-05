package com.thinksns.components;
import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.android.ThinksnsSend;
import com.thinksns.android.ThinksnsUserInfo;
import com.thinksns.android.ThinksnsWeiboComment;
import com.thinksns.android.ThinksnsWeiboContent;
import com.thinksns.model.Comment;
import com.thinksns.model.Weibo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public class CommentMyList extends SociaxList{
	private static final int SHOW_WEIBO=0;
	private static final int SHOW_USER =1;
	private static final int REPLY_COMMENT =2;
	private static View v;
	private  Comment comment;
	public CommentMyList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommentMyList(Context context) {
		super(context);
	}

	@Override
	protected void onClick(View view,int position,long id) {
		
		AlertDialog.Builder builder = new Builder(view.getContext());
		CommentListener listener = new CommentListener();
	//	comment = new Comment();
		comment = (Comment)this.getItemAtPosition(position);
		v = view;
		builder.setItems(R.array.commentopts, listener).setTitle(R.string.comment_menu_title).setCancelable(true).show();
	}
	
	private class CommentListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Thinksns app = (Thinksns)getContext().getApplicationContext();
			Bundle data = new Bundle();
			switch (which){
			case SHOW_WEIBO:
				LinearLayout layout = (LinearLayout)v.findViewById(R.id.weibo_data);
				Weibo weiboData = (Weibo)layout.getTag();
				data.putString("data", weiboData.toJSON());
				data.putInt("position", getLastPosition());
				app.startActivity(getActivityObj(), ThinksnsWeiboContent.class,data);
				break;
			case SHOW_USER:
			//	ImageView userheader = (ImageView)v.findViewById(R.id.user_header);
			//	User user = (User)userheader.getTag();
				data.putInt("uid", comment.getUid());
				app.startActivity(getActivityObj(),ThinksnsUserInfo.class,data);
				break;
			case REPLY_COMMENT:
				data.putInt("send_type", ThinksnsAbscractActivity.COMMENT);
				//Comment comment = (Comment)v.getTag();
				data.putInt("commentId", comment.getCommentId());
				data.putString("username", comment.getUname());
				data.putString("data",comment.getStatus().toJSON());
				app.startActivity(getActivityObj(),ThinksnsSend.class,data);
				break;
			}
		}
		
	}
}
