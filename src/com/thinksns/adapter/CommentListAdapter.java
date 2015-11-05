package com.thinksns.adapter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiStatuses;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.TimeIsOutFriendly;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.Comment;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.Weibo;
import com.thinksns.unit.TSUIUtils;
import com.thinksns.unit.TimeHelper;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommentListAdapter extends SociaxListAdapter {


	private static TextView username;
	private static TextView content;
	private static TextView cTime;
	private static Weibo weibo;
	private static HashMap<String,Integer> buttonSet;

	
	public CommentListAdapter(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list,
			Weibo weibo
			){
		super(context, list);
		buttonSet = new HashMap<String,Integer>();
		CommentListAdapter.weibo = weibo;
		super.isRefreshActivity = "ThinksnsWeiboContentList";
	}
	
	@Override
	public Comment getFirst() {
		return (Comment) super.getFirst();
	}

	@Override
	public Comment getLast() {
		return (Comment) super.getLast();
	}

	@Override
	public void doRefreshHeader() {
		//addRightButtonAnimAfter();
		super.doRefreshHeader();
	}
	
	@Override
	protected void refreshNewWeiboList(){
		//addRightButtonAnimAfter();
		super.refreshNewWeiboList();
	}
	
//	private void addRightButtonAnimAfter(){
//		buttonSet.put("left", refresh.getPaddingLeft());
//		buttonSet.put("right", refresh.getPaddingRight());
//		buttonSet.put("buttom", refresh.getPaddingBottom());
//		buttonSet.put("top", refresh.getPaddingTop());
//		buttonSet.put("height", refresh.getHeight());
//		buttonSet.put("width", refresh.getWidth());
//		
//		TextView view = (TextView)refresh;
//		view.setHeight(16);
//		view.setWidth(16);
//		view.setText("");
//	}
	
	@Override
	protected void cleanRightButtonAnim(View v) {
		//判断类型
		if (v instanceof TextView) {
			TextView view = (TextView) v;
			view.setHeight(16);
			view.setWidth(16);
			String test = context.getString(R.string.comment);
			view.setText(test);
			view.setClickable(true);
			// view.setPadding(buttonSet.get("left"), buttonSet.get("top"),
			// buttonSet.get("right"), buttonSet.get("b"));
			view.setBackgroundResource(context.getCustomTitle()
					.getRightResource());
			view.clearAnimation();
		}
	}
	
	
	@Override
	public Comment getItem(int position) {
		return (Comment) super.getItem(position);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = this.inflater.inflate(R.layout.comment_item, null);
		}
		
//		if(position == this.list.size() - 1 && refresh.isClickable()){
//			doRefreshFooter();
//		}
		username = (TextView)convertView.findViewById(R.id.username);
		content  = (TextView)convertView.findViewById(R.id.comment_content);
		cTime    = (TextView)convertView.findViewById(R.id.comment_time);
		Comment data = this.getItem(position);
		convertView.setTag(weibo.toJSON());
		username.setText(data.getUname());
		
		String patternStr = data.getContent();
		try{
			Pattern pattern =Pattern.compile("((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)") ;
			Matcher matcher = pattern.matcher(patternStr);
			SpannableString ss = new SpannableString(patternStr);
			while(matcher.find()){              
				ss.setSpan(new ForegroundColorSpan(Color.argb(255, 54, 92, 124)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			TSUIUtils.highlightContent(getContext(), ss);
			content.setText(ss);
		}catch(Exception ex){
			content.setText(data.getContent());
		}
		content.setTag(data);
			try {
				cTime.setText(TimeHelper.friendlyTime(data.getTimestemp()));
			} catch (TimeIsOutFriendly e) {
				// TODO Auto-generated catch block
				cTime.setText(data.getcTime());
			}
		
		return convertView;
	}



	@Override
	public ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException {
		return getApiStatuses().commentForWeiboHeaderTimeline(weibo, (Comment)obj, PAGE_COUNT);
	}



	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		return getApiStatuses().commentForWeiboFooterTimeline(weibo, (Comment)obj, PAGE_COUNT);
	}



	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return getApiStatuses().commentForWeiboTimeline(weibo,count);
	}
	
	private ApiStatuses getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getStatuses();
	}


}
