package com.thinksns.adapter;
import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiStatuses;
import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.TimeIsOutFriendly;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.Comment;
import com.thinksns.model.NotifyCount;
import com.thinksns.model.ReceiveComment;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;
import com.thinksns.unit.ListViewAppend;
import com.thinksns.unit.TSUIUtils;
import com.thinksns.unit.TimeHelper;

import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CommentMyListAdapter extends SociaxListAdapter {
	


	

	@Override
	public ReceiveComment getFirst() {
		// TODO Auto-generated method stub
		return (ReceiveComment) super.getFirst();
	}



	@Override
	public ReceiveComment getLast() {
		// TODO Auto-generated method stub
		return (ReceiveComment) super.getLast();
	}



	@Override
	public ReceiveComment getItem(int position) {
		// TODO Auto-generated method stub
		return (ReceiveComment) super.getItem(position);
	}


	public CommentMyListAdapter(ThinksnsAbscractActivity context, ListData<SociaxItem> data) {
		super(context,data);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentMyListItem commentMyListItem = null;
        if(commentMyListItem == null){
        	commentMyListItem = new CommentMyListItem();
        	convertView = this.inflater.inflate(R.layout.comment_my_list, null);
        	commentMyListItem.userheader = (ImageView)convertView.findViewById(R.id.user_comment_header);
    		commentMyListItem.username   = (TextView)convertView.findViewById(R.id.user_name);
    		commentMyListItem.time       = (TextView)convertView.findViewById(R.id.comment_ctime);
    		commentMyListItem.content	   = (TextView)convertView.findViewById(R.id.comment_content);
    		commentMyListItem.myComment  = (TextView)convertView.findViewById(R.id.comment_receive_me);
    		convertView.setTag(commentMyListItem);
        }else{
        	commentMyListItem = (CommentMyListItem)convertView.getTag();
        }
        	LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.weibo_data);
    		this.loadingHeader(position,commentMyListItem.userheader);
    		ReceiveComment comment= this.getItem(position);
    		layout.setTag(comment.getStatus());
    		commentMyListItem.username.setText(comment.getUname());
    		try {
    			commentMyListItem.time.setText(TimeHelper.friendlyTime(comment.getTimestemp()));
    		} catch (TimeIsOutFriendly e) {
    			commentMyListItem.time.setText(comment.getcTime());
    		}
    		SpannableStringBuilder ss = new SpannableStringBuilder(comment.getContent());
			TSUIUtils.highlightContent(getContext(), ss);
    		commentMyListItem.content.setText(ss);
    		if(comment.isNullForReplyComment()){
    			SpannableStringBuilder spStringBuilder = new SpannableStringBuilder("回复我的微博:"+comment.getStatus().getContent());
    			TSUIUtils.highlightContent(getContext(), spStringBuilder);
    			commentMyListItem.myComment.setText(spStringBuilder);
    		}else{
    			SpannableStringBuilder spStringBuilder = new SpannableStringBuilder("回复我的评论:"+comment.getReplyComment().getContent());
    			TSUIUtils.highlightContent(getContext(), spStringBuilder);
    			commentMyListItem.myComment.setText(spStringBuilder);
    		}
    		return convertView;
        }
	/*	CommentMyListItem commentMyListItem = new CommentMyListItem();
		if(convertView ==null){
			convertView = this.inflater.inflate(R.layout.comment_my_list, null);
		}
		boolean canRefresh = false;
		if(refresh == null){
			canRefresh = true;
		}else{
			canRefresh = refresh.isClickable();
		}
		if(position == this.list.size()-1 && canRefresh){
			//doRefreshFooter();
		}
		
		ReceiveComment comment= (ReceiveComment)this.getItem(position);
		commentMyListItem.userheader = (ImageView)convertView.findViewById(R.id.user_comment_header);
		this.loadingHeader(position,commentMyListItem.userheader);
		
		commentMyListItem.username   = (TextView)convertView.findViewById(R.id.user_name);
		commentMyListItem.time       = (TextView)convertView.findViewById(R.id.comment_ctime);
		commentMyListItem.content	   = (TextView)convertView.findViewById(R.id.comment_content);
		commentMyListItem.myComment  = (TextView)convertView.findViewById(R.id.comment_receive_me);
		LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.weibo_data);
		layout.setTag(comment.getStatus());
		convertView.setTag(comment);

		commentMyListItem.username.setText(comment.getUname());
		try {
			commentMyListItem.time.setText(TimeHelper.friendlyTime(comment.getTimestemp()));
		} catch (TimeIsOutFriendly e) {
			commentMyListItem.time.setText(comment.getcTime());
		}
		
		commentMyListItem.content.setText(comment.getContent());
		if(comment.isNullForReplyComment()){
			commentMyListItem.myComment.setText("回复我的微博:"+comment.getStatus().getContent());
		}else{
			commentMyListItem.myComment.setText("回复我的评论:"+comment.getReplyComment().getContent());
		}
		
		return convertView;*/



	private User loadingHeader(int position,ImageView userheader) {
		User temp = getItem(position).getUser();
		userheader.setTag(temp);
		Thinksns app = (Thinksns)this.context.getApplicationContext();
		if(temp.hasHeader()&&app.isNetWorkOn()){
			if(temp.isNullForHeaderCache()){
				dowloaderTask(temp.getUserface(), userheader,BitmapDownloaderTask.Type.FACE);
			}else{
				Bitmap cache = temp.getHeader();
				if(cache == null){
					dowloaderTask(temp.getUserface(), userheader,BitmapDownloaderTask.Type.FACE);
				}else{
					userheader.setImageBitmap(cache);
				}
			}
		}
		return temp;
	}
	
	final protected  void dowloaderTask(String url,ImageView image,BitmapDownloaderTask.Type type) {
		BitmapDownloaderTask task = new BitmapDownloaderTask(image,type);
		task.execute(url);
	}



	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem> commentDatas = getApiStatuses()
				.commentReceiveMyHeaderTimeline((ReceiveComment) obj,
						PAGE_COUNT);
		this.getApiUsers().unsetNotificationCount(
				NotifyCount.Type.weibo_comment, getMyUid());
		Thinksns app = (Thinksns) this.context.getApplicationContext();
		app.getMyCommentSql().deleteWeibo(commentDatas.size());
		if (commentDatas.size() > 0) {
			for (int i = 0; i < commentDatas.size(); i++) {
				app.getMyCommentSql().addComment(
						(ReceiveComment) commentDatas.get(i), getMySite(),
						getMyUid());
			}
		}
		return commentDatas;
	}


	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		return getApiStatuses().commentReceiveMyFooterTimeline((Comment)obj, PAGE_COUNT);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem>  commentDatas = getApiStatuses().commentReceiveMyTimeline(count);
		this.getApiUsers().unsetNotificationCount(
				NotifyCount.Type.weibo_comment, getMyUid());
		Thinksns app = (Thinksns)this.context.getApplicationContext();
		if(this.list.size() == 0){
			for(int i=0;i<commentDatas.size();i++){
				app.getMyCommentSql().addComment((ReceiveComment)commentDatas.get(i),getMySite(),getMyUid());
			}			
		}
		return commentDatas;
	}
	
	private ApiStatuses getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getStatuses();
	}
	
	public class CommentMyListItem{
		ImageView userheader;
		TextView username;
		TextView time;
		TextView content;
		TextView myComment;
	}

}
