package com.thinksns.unit;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.thinksns.android.R;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.exceptions.TimeIsOutFriendly;
import com.thinksns.model.Weibo;

public class ListViewAppend extends WeiboDataSet {
	private ImageView image;
	private Activity activityContent;
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	@Override
	protected  int getContentIndex(){
		return CONTENT_INDEX;
	}
	public ListViewAppend(ThinksnsAbscractActivity obj){
		this.activityContent = obj;
	}
	@Override
	
	/**
	 * 对在ListView中显示的Weibodata进行布局配置
	 */
	public void appendWeiboData(Weibo weibo, View view) {
		// TODO Auto-generated method stub
			WeiboDataItem weiboDataItem = null;
			if(weiboDataItem == null){
				weiboDataItem = new WeiboDataItem();
				weiboDataItem.username = (TextView)view.findViewById(R.id.user_name);
				weiboDataItem.weiboCtime = (TextView)view.findViewById(R.id.weibo_ctime);
				weiboDataItem.weiboContent = (TextView)view.findViewById(R.id.weibo_content);
				weiboDataItem.header = (ImageView)view.findViewById(R.id.user_header);
				view.setTag(weiboDataItem);
			}else{
				weiboDataItem = (WeiboDataItem)view.getTag();
			}
			
			LinearLayout layout = (LinearLayout)view.findViewById(R.id.weibo_data);
			layout.setTag(weibo);
			TextPaint paint = weiboDataItem.username.getPaint();
			paint.setFakeBoldText(true); 
            weiboDataItem.username.setText(weibo.getUsername());
			try {
				weiboDataItem.weiboCtime.setText(TimeHelper.friendlyTime(weibo.getTimestamp()));
			} catch (TimeIsOutFriendly e) {
				weiboDataItem.weiboCtime.setText(weibo.getCtime());
			}
			
			//判断是否该微博已被管理员删除 Modified at 13-08-07 lizihao 
			if(weibo.isDelByAdmin()){
				weiboDataItem.weiboContent.setText("对不起，该微博已被系统管理员删除。");
			}else{
			try{
				Pattern pattern =Pattern.compile("((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)") ;
				Matcher matcher = pattern.matcher(weibo.getContent());
				SpannableString ss = new SpannableString(weibo.getContent());
				while(matcher.find()){              
					ss.setSpan(new ForegroundColorSpan(Color.argb(255, 54, 92, 124)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				TSUIUtils.highlightContent(view.getContext(), ss);
				weiboDataItem.weiboContent.setText(ss);
				
			}catch(Exception ex){
				weiboDataItem.weiboContent.setText(weibo.getContent());
			}
			}
			
		   
			setCommentCount(weibo,view);
			setTranspondCount(weibo,view);
			setWeiboFrom(weibo, view);
			setCountLayout(weibo,view);
			
			//添加头像
			addHeader(weibo,view,weiboDataItem.header);
			removeViews(layout);
			if(!weibo.isNullForTranspond()||weibo.getTranspondId()>0){
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
				layout.addView(appendTranspond(weibo.getTranspond(), view),getContentIndex(),lp);
			}
			if(weibo.hasImage() && !weibo.isDelByAdmin()){
				LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(getThumbWidth(),getThumbHeight());
				lpImage.gravity = Gravity.LEFT;
				lpImage.setMargins(0, 10,0,5);
				image =(ImageView) appendImage(weibo, layout);
				image.setOnClickListener(((ThinksnsAbscractActivity)activityContent).getImageFullScreen(weibo.getThumbMiddleUrl()));
				layout.addView(image,getContentIndex(),lpImage);
			}
			view.setTag(weiboDataItem);
	}

	/**
	 * 转发
	 */
	@Override
	public View appendTranspond(Weibo weibo, View view) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		LinearLayout layout = new LinearLayout(view.getContext());
		layout.setLayoutParams(lp);
		layout.setBackgroundResource(R.drawable.reviewboxbg);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		TextView content = new TextView(view.getContext());
		LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		lpText.gravity=Gravity.CENTER;
		lpText.setMargins(12, 8, 12, 10);
		content.setLineSpacing( 2.669983f,1.0f);
		
		//设置名字
		content.setText((weibo.getUsername()+weibo.getContent()),TextView.BufferType.SPANNABLE);
		String patternStr='@'+weibo.getUsername()+": "+weibo.getContent();

		try{
			Pattern pattern =Pattern.compile("((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)") ;
			Matcher matcher = pattern.matcher(patternStr);
			
			SpannableString spannableString = new SpannableString(patternStr);
			while(matcher.find()){ 
				//spannableString.setSpan(new ForegroundColorSpan(R.color.urlColor), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				spannableString.setSpan(new ForegroundColorSpan(Color.argb(255, 54, 92, 124)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				//ss.setSpan(new URLSpan(matcher.group()), matcher.start(), matcher.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			
			TSUIUtils.highlightContent(view.getContext(), spannableString);
			
			content.setText(spannableString);
		}catch(Exception ex){
			content.setText(weibo.getContent());
		}
		
		content.setTextColor(view.getContext().getResources().getColor(R.color.tranFontColor));
		//content.setTextSize(view.getContext().getResources().getDimension(R.dimen.tranFont));
		content.setTextSize(15);
		layout.addView(content,lpText);
		if(weibo.hasImage()&& !weibo.isDelByAdmin()){
			LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(getThumbWidth(),getThumbHeight());
			lpImage.gravity = getGravity();
			//设置
			lpImage.setMargins(12, 2, 12, 12);
			image =(ImageView) appendImage(weibo, view);
			image.setOnClickListener(((ThinksnsAbscractActivity)activityContent).getImageFullScreen(weibo.getThumbMiddleUrl()));
			layout.addView(image, lpImage);
		}
		layout.setId(TRANSPOND_LAYOUT);
		return layout;
	}

	/*protected  void setCountLayout(Weibo weibo,View view){
		RelativeLayout countLayout = (RelativeLayout)view.findViewById(R.id.weibo_count_layout);
		if(weibo.isNullForComment() && weibo.isNullForTranspondCount()){
			
			countLayout.setVisibility(View.GONE);
		}else{
			countLayout.setVisibility(View.VISIBLE);
		}
	}*/
	
	/**
	 * weibo_count_layout为ListView中每一个weibo对象的来源，评论数及转发数
	 * 如果这条微博被管理员删除那么这个嵌套布局将被设置为不可见。
	 */
	@Override
	protected  void setCountLayout(Weibo weibo,View view){
		RelativeLayout countLayout = (RelativeLayout)view.findViewById(R.id.weibo_count_layout);
		if(!weibo.isDelByAdmin()){
		  countLayout.setVisibility(View.VISIBLE);
		}else{
		  countLayout.setVisibility(View.INVISIBLE);
		}
	}
	
	
	/**
	 * 控制主页ListViewItem布局
	 * @param weibo
	 * @param view
	 */
	
	protected void setWeiboFrom(Weibo weibo,View view){
		TextView tvFrom = (TextView)view.findViewById(R.id.weibo_from);
		tvFrom.setText("来自 " + TSUIUtils.replaseFrome(weibo.getFrom()+""));
	}
	
	@Override
	public  void setTranspondCount(Weibo weibo,View view){
		TextView transpondCount = (TextView)view.findViewById(R.id.transpond_count);
		transpondCount.setText(weibo.getTranspondCount()+"");

	}
	
	@Override
	public  void setCommentCount(Weibo weibo,View view){
		TextView commentCount = (TextView)view.findViewById(R.id.comment_count);
		commentCount.setText(weibo.getComment()+"");
	}
	
	@Override
	protected  int getGravity(){
		return Gravity.LEFT;
	}
	
	@Override
	protected  BitmapDownloaderTask.Type getThumbType(){
		return BitmapDownloaderTask.Type.THUMB;
	}
	
	@Override
	protected  boolean hasThumbCache(Weibo weibo){
		return weibo.isNullForThumbCache();
	}
	
	@Override
	protected  String getThumbUrl(Weibo weibo){
		return weibo.getThumbUrl();
	}
	
	@Override
	protected  Bitmap getThumbCache(Weibo weibo){
		return weibo.getThumb();
	}
	
	

}
