package com.thinksns.unit;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.thinksns.android.R;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.android.ThinksnsWeiboContentList;
import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.exceptions.TimeIsOutFriendly;
import com.thinksns.model.Weibo;

public class WeiboContent extends WeiboDataSet {
	private  Activity activityContent;
	private String temp;
	private ImageView image;
	
	public WeiboContent(ThinksnsAbscractActivity obj){
		this.activityContent = obj;
	}
	@Override
	public void appendWeiboData(Weibo weibo, View view) {
		// TODO Auto-generated method stub
		WeiboDataItem weiboConententItem = new WeiboDataItem();
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.weibo_data);
		weiboConententItem.username = (TextView)view.findViewById(R.id.user_name);
		weiboConententItem.weiboCtime = (TextView)view.findViewById(R.id.weibo_ctime);
		weiboConententItem.weiboContent = (TextView)view.findViewById(R.id.weibo_content);
		weiboConententItem.header = (ImageView)view.findViewById(R.id.user_header);
		layout.setTag(weibo);
		weiboConententItem.username.setText(weibo.getUsername());
		//weiboConententItem.username.setText("TestALL");
		try {
			weiboConententItem.weiboCtime.setText(TimeHelper.friendlyTime(weibo.getTimestamp()));
		} catch (TimeIsOutFriendly e) {
			weiboConententItem.weiboCtime.setText(weibo.getCtime());
		}
		
		try{
			SpannableStringBuilder ss = new SpannableStringBuilder();
			ss = dealWeiboContent(weibo.getContent(),weiboConententItem.weiboContent) ;
			TSUIUtils.highlightContent(view.getContext(), ss);
			weiboConententItem.weiboContent.setText(ss);
			//dealWeiboContent(weibo.getContent(),weiboConententItem.weiboContent);
			//setUrlSpans(weiboConententItem.weiboContent);
		}catch(Exception ex){
			weiboConententItem.weiboContent.setText(weibo.getContent());
		}
		
		
		setCommentCount(weibo,view);
		setTranspondCount(weibo,view);
		setWeiboFrom(weibo, view);
		setCountLayout(weibo,view);
		
		addHeader(weibo,view,weiboConententItem.header);
		
		removeViews(layout);
		
		if(!weibo.isNullForTranspond()){
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			layout.addView(appendTranspond(weibo.getTranspond(), view),getContentIndex(),lp);
		}
		if(weibo.hasImage()){
			LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(getThumbWidth(),getThumbHeight());
			lpImage.gravity = Gravity.LEFT;
			lpImage.setMargins(0, 10, 0, 10);
			image = (ImageView)appendImage(weibo, view);
			//与微博正文类一致
			image.setOnClickListener(((ThinksnsWeiboContentList)activityContent).getImageFullScreen(weibo.getThumbMiddleUrl()));
			layout.addView(image,getContentIndex(),lpImage);
		}
		ImageButton favorite = (ImageButton)view.findViewById(R.id.button_favorite);
		if(weibo.isFavorited()){
			favorite.setImageResource(R.drawable.button_is_favorite);
			favorite.setTag(ThinksnsWeiboContentList.FavoriteStatus.YES);
		}else{
			favorite.setImageResource(R.drawable.button_favorite);
			favorite.setTag(ThinksnsWeiboContentList.FavoriteStatus.NO);
		}
	}

	
	
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
		//lpText.setMargins(12, 8, 12, 10);
		content.setPadding(12, 8, 12, 10);
		content.setLineSpacing( 2.669983f,1.0f);
		content.setText(weibo.getContent(),TextView.BufferType.SPANNABLE);
		content.setText((weibo.getUsername()+weibo.getContent()),TextView.BufferType.SPANNABLE);
		String patternStr='@'+weibo.getUsername()+": "+weibo.getContent();
		
		try {
			SpannableStringBuilder ss = new SpannableStringBuilder();
			ss = dealWeiboContent(patternStr, content) ;
			TSUIUtils.highlightContent(view.getContext(), ss);
			content.setText(ss);
			//content.setTextSize(R.dimen.commentSize);
		} catch (Exception ex) {
			content.setText(weibo.getContent());
		}
		
		content.setTextColor(view.getContext().getResources().getColor(R.color.font));
		content.setTextSize(15);
		layout.addView(content,lpText);
		if(weibo.hasImage()){
			LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(getThumbWidth(),getThumbHeight());
			lpImage.gravity = getGravity();
			image = (ImageView)appendImage(weibo, view);
			image.setOnClickListener(((ThinksnsWeiboContentList)activityContent).getImageFullScreen(weibo.getPicUrl()));
			layout.addView(image, lpImage);
		}
		layout.setId(TRANSPOND_LAYOUT);
		return layout;
	}



	@Override
	protected  int getContentIndex(){
		return 1;
	}
	
	@Override
	protected  void setCountLayout(Weibo weibo,View view){
	
	}
	
	protected void setWeiboFrom(Weibo weibo,View view){
		TextView tvFrom = (TextView)view.findViewById(R.id.weibo_from);
		tvFrom.setText("来自 " + TSUIUtils.replaseFrome(weibo.getFrom()+""));
	}
	
	@Override
	public  void setTranspondCount(Weibo weibo,View view){
		Button transpondCount = (Button)view.findViewById(R.id.button_transpond);
		transpondCount.setText(view.getContext().getString(R.string.transpond)+"("+weibo.getTranspondCount()+")");

	}
	
	@Override
	public  void setCommentCount(Weibo weibo,View view){
		Button transpondCount = (Button)view.findViewById(R.id.button_comment);
		transpondCount.setText(view.getContext().getString(R.string.comment)+"("+weibo.getComment()+")");
	}
	
	@Override
	protected  int getGravity(){
		return Gravity.CENTER_HORIZONTAL;
	}
	
	@Override
	protected  BitmapDownloaderTask.Type getThumbType(){
		return BitmapDownloaderTask.Type.MIDDLE_THUMB;
	}
	
	@Override
	protected  boolean hasThumbCache(Weibo weibo){
		return weibo.isNullForThumbMiddleCache();
	}
	
	@Override
	protected  String getThumbUrl(Weibo weibo){
		return weibo.getThumbMiddleUrl();
	}
	
	@Override
	protected  Bitmap getThumbCache(Weibo weibo){
		return weibo.getThumbMiddle();
	}
	

	//Color.argb(255, 54, 92, 124)
	private SpannableStringBuilder dealWeiboContent(String weiboContent,
			TextView textView) {
		Pattern pattern = Pattern
				.compile("((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)");
		temp = weiboContent;
		Matcher matcher = pattern.matcher(temp);
		List<String> list = new LinkedList<String>();
		while (matcher.find()) {
			if (!list.contains(matcher.group())) {
				temp = temp.replace(
						matcher.group(),
						"<a href=\"" + matcher.group() + "\">"
								+ matcher.group() + "</a>");
			}
			list.add(matcher.group());
		}
		textView.setText(Html.fromHtml(temp));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		CharSequence text = textView.getText();
		if (text instanceof Spannable) {
			int end = text.length();
			Spannable sp = (Spannable) textView.getText();
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			style.clearSpans();
			// ThinksnsWeiboContentList 与微博正文类一致
			for (URLSpan url : urls) {
				style.setSpan(((ThinksnsWeiboContentList) activityContent)
						.typeClick(url.getURL()), sp.getSpanStart(url), sp
						.getSpanEnd(url), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			return style;
		}
		return null;
	}
	
}



