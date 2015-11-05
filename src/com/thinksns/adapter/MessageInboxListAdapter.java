package com.thinksns.adapter;
import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiMessage;
import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.TimeIsOutFriendly;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.Message;
import com.thinksns.model.ListData;
import com.thinksns.model.NotifyCount;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;
import com.thinksns.unit.ListViewAppend;
import com.thinksns.unit.TimeHelper;

import android.graphics.Bitmap; 

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;


/**
 * 私信列表Adapter
 * @author Administrator
 *
 */
public class MessageInboxListAdapter extends SociaxListAdapter {
	
	


	private static ListViewAppend append;
	

	@Override
	public Message getFirst() {
		return (Message) super.getFirst();
	}



	@Override
	public Message getLast() {
		return (Message) super.getLast();
	}



	@Override
	public Message getItem(int position) {
		return (Message) super.getItem(position);
	}


	public MessageInboxListAdapter(ThinksnsAbscractActivity context, ListData<SociaxItem> data) {
		super(context,data);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageItem messageItem = null;
		if(messageItem==null){
			messageItem  = new MessageItem();
			convertView = this.inflater.inflate(R.layout.inbox, null);
			messageItem.userheader = (ImageView)convertView.findViewById(R.id.user_message_header);
			messageItem.username   = (TextView)convertView.findViewById(R.id.user_name);
    		messageItem.time       = (TextView)convertView.findViewById(R.id.message_ctime);
    		messageItem.content	   = (TextView)convertView.findViewById(R.id.message_content);
    		convertView.setTag(messageItem);
		}else{
			messageItem = (MessageItem)convertView.getTag();
		}
    		boolean canRefresh = false;
    		if(refresh == null){
    			canRefresh = true;
    		}else{
    			canRefresh = refresh.isClickable();
    		}
    		if(position == this.list.size()-1 && canRefresh && position>=10){
    			doRefreshFooter();
    		}
    		Message message= this.getItem(position);
    		this.loadingHeader(position,messageItem.userheader);
    		messageItem.username.setText(message.getFromUname());
    		try {
    			messageItem.time.setText(TimeHelper.friendlyTime(message.getMtime()));
    		} catch (TimeIsOutFriendly e) {
    			messageItem.time.setText(message.getCtime());
    		}
    		messageItem.content.setText(message.getContent());
        return convertView;
	}
	
/*	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = this.inflater.inflate(R.layout.inbox, null);
		}
		boolean canRefresh = false;
		if(refresh == null){
			canRefresh = true;
		}else{
			canRefresh = refresh.isClickable();
		}
		if(position == this.list.size()-1 && canRefresh){
			doRefreshFooter();
		}
		MessageItem messageItem = new MessageItem();
		Message message= (Message)this.getItem(position);
		
		messageItem.userheader = (ImageView)convertView.findViewById(R.id.user_message_header);
		this.loadingHeader(position,messageItem.userheader);
		
		messageItem.username   = (TextView)convertView.findViewById(R.id.user_name);
		messageItem.time       = (TextView)convertView.findViewById(R.id.message_ctime);
		messageItem.content	   = (TextView)convertView.findViewById(R.id.message_content);
		convertView.setTag(message);
		messageItem.username.setText(message.getFromUname());
		try {
			messageItem.time.setText(TimeHelper.friendlyTime(message.getMtime()));
		} catch (TimeIsOutFriendly e) {
			messageItem.time.setText(message.getCtime());
		}
		messageItem.content.setText(message.getContent());
		return convertView;
	}*/
	
	private User loadingHeader(int position,ImageView userheader) {
		Message message = getItem(position);
		User temp = new User();
		temp.setUid(message.getFromUid());
		temp.setUserName(message.getFromUname());
		temp.setFace(message.getFromFace());
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
	public ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException {
		
		ListData<SociaxItem>  messageDatas = getApiStatuses().inboxHeader((Message)obj, PAGE_COUNT);
		this.getApiUsers().unsetNotificationCount(NotifyCount.Type.message, getMyUid());
		/*Thinksns app = (Thinksns)this.context.getApplicationContext();
		app.getWeiboSql().deleteWeibo(messageDatas.size());
		if(messageDatas.size()>0){
			for(int i=0;i<messageDatas.size();i++){
				app.getMyMessageSql().addMessage((Message)messageDatas.get(i));
			}
		}*/
		return messageDatas;		
		
	//	return getApiStatuses().inboxHeader((Message)obj, PAGE_COUNT);
	}



	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		return getApiStatuses().inboxFooter((Message)obj, PAGE_COUNT);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		
		ListData<SociaxItem>  messageDatas = getApiStatuses().inbox(count);
		this.getApiUsers().unsetNotificationCount(NotifyCount.Type.message, getMyUid());
		/*Thinksns app = (Thinksns)this.context.getApplicationContext();
		if(this.list.size() == 0){
			for(int i=0;i<messageDatas.size();i++){
				app.getMyMessageSql().addMessage((Message)messageDatas.get(i));
			}			
		}*/
		
		return messageDatas;
	}
	
	private ApiMessage getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getMessages();
	}

	public class MessageItem{
		TextView username;
		ImageView userheader;
		TextView content;
		TextView time;
	}
}
