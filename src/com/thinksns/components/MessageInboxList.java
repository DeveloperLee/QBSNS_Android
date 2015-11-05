package com.thinksns.components;
import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.android.ThinksnsMessageDetail;
import com.thinksns.android.ThinksnsSend;
import com.thinksns.android.ThinksnsUserInfo;
import com.thinksns.android.ThinksnsWeiboComment;
import com.thinksns.model.Message;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.util.AttributeSet;

import android.view.View;


public class MessageInboxList extends SociaxList{
	private static final int REPLY_MESSAGE=0;
	private static final int SHOW_USER =1;
	private static final int SHOW_DETAIL =2;
	private static View v;
	private Message message;
	public MessageInboxList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MessageInboxList(Context context) {
		super(context);
	}

	@Override
	protected void onClick(View view,int position,long id) {
		AlertDialog.Builder builder = new Builder(view.getContext());
		CommentListener listener = new CommentListener();
		message = (Message)this.getItemAtPosition(position);
		v = view;
		builder.setItems(R.array.inboxopts, listener).setTitle(R.string.weibo_menu_title).setCancelable(true).show();
	}
	
	private class CommentListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Thinksns app = (Thinksns)getContext().getApplicationContext();
			Bundle data = new Bundle();
			switch (which){
			case REPLY_MESSAGE:
				//Message message = (Message)v.getTag();
				data.putInt("messageId", message.getListId());
				data.putInt("send_type", ThinksnsAbscractActivity.REPLY_MESSAGE);
				//app.getActivityStack().startActivity(getActivityObj(), ThinksnsSend.class,data);
				app.startActivity(getActivityObj(), ThinksnsSend.class,data);
				break;
			case SHOW_USER:
			//	ImageView userheader = (ImageView)v.findViewById(R.id.user_header);
				//User user = (User)userheader.getTag();
				data.putInt("uid", message.getFromUid());
				app.startActivity(getActivityObj(),ThinksnsUserInfo.class,data);
				break;
			case SHOW_DETAIL:
				//Message messageInfo = (Message)v.getTag();
				data.putInt("messageId", message.getListId());
				app.startActivity(getActivityObj(), ThinksnsMessageDetail.class,data);
				break;
			}
		}
		
	}
}
