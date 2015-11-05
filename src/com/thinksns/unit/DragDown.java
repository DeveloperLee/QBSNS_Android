package com.thinksns.unit;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.thinksns.android.R;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.listener.OnTouchListListener;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

public class DragDown implements OnTouchListener,OnGestureListener,OnTouchListListener {
	private GestureDetector mGestureDetector;
	private static final int CONTENT_TEXT_ID=0;
	private static final int CONTENT_TIME_ID = 1;
	private static final int CONTENT_LAYOUT_ID = 2;
	private static final int CONTENT_IMAGE_ID = 3;
	private static final int FOOTER_IMAGE_ID  = 3;
	private static final int OFFSET = -100;
	private static final int IMAGE_LEFT_MARGIN = 40;
	private static final int IMAGE_RIGHT_MARGIN = 80;
	private static final int BOTTON_MARIGN = 20;

	private static final int MIN_OFFSET = 0;
	
	private static boolean hasHeader = false;
	private long lastRefresh;


	private static boolean refreshing = false;
	private static boolean hasReverse = false;
	private static boolean hasTouch   = false;
	
	private LinearLayout header;
	private LinearLayout footer;
	private LinearLayout headerContent;
	private LinearLayout footerContent;

	
	private static Animation anim;
	private static Animation anim_down;
	private  ThinksnsAbscractActivity activityObj;
	private  Context context;
	
	private ListView view;
	private static boolean viewHasHeader = false;
	private static boolean viewHasFooter = false;
	
	public DragDown(Context context,ListView v){
		mGestureDetector = new GestureDetector(context,this);
		setContext(context);
		setView(v);
		setActivityObj((ThinksnsAbscractActivity)context);
	}

	
	public void setContext(Context context) {
		this.context = context;
	}


	public ThinksnsAbscractActivity getActivityObj() {
		return activityObj;
	}


	public void setActivityObj(ThinksnsAbscractActivity activityObj) {
		this.activityObj = activityObj;
	}


	public ListView getView() {
		return view;
	}


	public void setView(ListView view) {
		this.view = view;
	}


	public View getHeaderView(){
		anim = AnimationUtils.loadAnimation(getContext(), R.anim.reverse_up);
		anim_down =  AnimationUtils.loadAnimation(getContext(), R.anim.reverse_down);
		anim.setFillEnabled(true);
		anim.setFillAfter(true);
		anim_down.setFillEnabled(true);
		anim_down.setFillAfter(true);
		
		
		LinearLayout.LayoutParams lpCenter = this.getLinearLayout();
		header = new LinearLayout(getContext());
		header.setOrientation(LinearLayout.HORIZONTAL);
		headerContent = new LinearLayout(getContext());
		headerContent.setId(CONTENT_LAYOUT_ID);
		headerContent.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams lpText = new  LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lpText.gravity=Gravity.CENTER;
		
		TextView contentText = new TextView(getContext());
		contentText.setId(CONTENT_TEXT_ID);
		contentText.setText(R.string.please_up);
		TextView contentTime        = new TextView(getContext());
		contentTime.setId(CONTENT_TIME_ID);
		headerContent.addView(contentText,lpText);
		headerContent.addView(contentTime,lpText);
		
		
		ImageView arrow = new ImageView(getContext());
		arrow.setId(CONTENT_IMAGE_ID);
		arrow.setBackgroundResource(R.drawable.arrow_down);
		LinearLayout.LayoutParams lpImage = this.getLinearLayout();
		lpImage.leftMargin = IMAGE_LEFT_MARGIN;
		lpImage.rightMargin = IMAGE_RIGHT_MARGIN;
		lpImage.bottomMargin = BOTTON_MARIGN;
		header.addView(arrow,lpImage);
		header.addView(headerContent, lpCenter);
		this.setTime();
		viewHasHeader = true;
		return header;
	}
	
	public View getFooterView(){
		LinearLayout.LayoutParams lpCenter = this.getLinearLayout();
		footer = new LinearLayout(getContext());
		footer.setOrientation(LinearLayout.HORIZONTAL);
		footerContent = new LinearLayout(getContext());
		footerContent.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams lpText = new  LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lpText.gravity=Gravity.CENTER;
		
		TextView contentText = new TextView(getContext());
		contentText.setText(R.string.refreshing);
		footerContent.addView(contentText,lpText);
		
		
		ImageView arrow = new ImageView(getContext());
		arrow.setId(FOOTER_IMAGE_ID);
		LinearLayout.LayoutParams lpImage = this.getLinearLayout();
		lpImage.leftMargin = IMAGE_LEFT_MARGIN;
		lpImage.rightMargin = IMAGE_RIGHT_MARGIN;
		lpImage.bottomMargin = BOTTON_MARIGN;
		footer.addView(arrow,lpImage);
		footer.addView(footerContent, lpCenter);
		viewHasFooter = true;
		return footer;
	}
	
	private LinearLayout.LayoutParams getLinearLayout(){
		LinearLayout.LayoutParams result = new  LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		result.gravity=Gravity.BOTTOM;
		result.topMargin=OFFSET;
		result.bottomMargin=BOTTON_MARIGN;
		return result;
	}
	
	public ThinksnsAbscractActivity getActivity(){
		return (ThinksnsAbscractActivity)this.context;
	}
	
	public boolean canDrag(){
		return refreshing || hasHeader;
	}
	
	
	private void setTime(){
		TextView contentTime    = (TextView)header.findViewById(CONTENT_TIME_ID);
		String time = new SimpleDateFormat("MM-dd HH:mm").format(new Date(this.lastRefresh));
		String text = this.getContext().getString(R.string.last_refresh)+time;
		contentTime.setText(text);
		contentTime.setGravity(Gravity.CENTER);
	}
	
	public Context getContext(){
		return this.context;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (null != headerContent) {
			LinearLayout.LayoutParams lpCenter = (android.widget.LinearLayout.LayoutParams) headerContent
					.getLayoutParams();
			if (!refreshing && event.getAction() == MotionEvent.ACTION_UP) {
				ThinksnsAbscractActivity activity = getActivityObj();
				View right = activity.getCustomTitle().getRight();
				boolean canRefresh = false;
				if (right == null) {
					canRefresh = true;
				} else {
					canRefresh = right.isClickable();
				}
				if (lpCenter.topMargin >= MIN_OFFSET && canRefresh) {
					Log.e("center", "cener=" + lpCenter.topMargin);
					activity.refreshHeader();
				} else {
					lpCenter.topMargin = OFFSET;
					headerContent.setLayoutParams(lpCenter);
				}
				hasTouch = false;
			}
		}
		if (refreshing) {
			return true;
		}
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		hasHeader = true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		hasHeader = false;
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if(this.checkCanScroll() ) return false;
		if (header == null) return false;
		TextView contentText = (TextView)header.findViewById(CONTENT_TEXT_ID);
		ImageView contentImage = (ImageView)header.findViewById(CONTENT_IMAGE_ID);
		
		if(headerContent == null){
			return false;
		}
		LinearLayout.LayoutParams lpCenter = (android.widget.LinearLayout.LayoutParams) headerContent.getLayoutParams();
		try{
			if(distanceY < 0 && e1.getY() >= e2.getY()){
				int height =(int) Math.ceil(Math.abs((int) (e1.getY() - e2.getY()))*0.5);
				lpCenter.topMargin = height-Math.abs(OFFSET);
				headerContent.setLayoutParams(lpCenter);
				hasTouch = true;
			}else{
				if(e2.getY() >= e1.getY()){
					int height = (int) Math.ceil(Math.abs((int) (e2.getY() - e1.getY()))*0.5);;
					
					lpCenter.topMargin = height+OFFSET;
					headerContent.setLayoutParams(lpCenter);
					hasTouch = true;
				}
			}
		}catch(Exception ex){
			return false;
		}
		if(lpCenter.topMargin < 0){
			if(hasReverse){
				
				contentImage.startAnimation(anim_down);
				hasReverse = false;
			}
			contentText.setText(R.string.please_down);
			contentText.setPadding(0, 7, 0, 0);
		}else{
			if(!hasReverse){
				contentImage.startAnimation(anim);
				hasReverse = true;
			}
			contentText.setText(R.string.please_up);
			contentText.setPadding(0, 7, 0, 0);
		}
		if(refreshing){
			return true;
		}
		return hasTouch;
	}

	@Override
	public void onLongPress(MotionEvent e) {		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}


	@Override
	public void headerShow() {
		LinearLayout.LayoutParams lpCenter = (android.widget.LinearLayout.LayoutParams) headerContent.getLayoutParams();
		lpCenter.topMargin=5;
		headerContent.setLayoutParams(lpCenter);
		refreshing = true;
	}

	@Override
	public void headerHiden() {
		try{
			LinearLayout.LayoutParams lpCenter = (android.widget.LinearLayout.LayoutParams) headerContent.getLayoutParams();
			lpCenter.topMargin=OFFSET;
			headerContent.setLayoutParams(lpCenter);
			refreshing = false;
			ImageView contentImage = (ImageView)header.findViewById(CONTENT_IMAGE_ID);
			LinearLayout.LayoutParams lpImage = (android.widget.LinearLayout.LayoutParams) contentImage.getLayoutParams();
			lpImage.bottomMargin = BOTTON_MARIGN;
			contentImage.setBackgroundResource(R.drawable.arrow_down);
		}catch(Exception ex){
			return;
		}
	}


	@Override
	public void headerRefresh() {
		this.setTime();
		TextView contentText = (TextView)header.findViewById(CONTENT_TEXT_ID);
		ImageView contentImage = (ImageView)header.findViewById(CONTENT_IMAGE_ID);
		
		contentText.setText(R.string.refreshing);
		contentText.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams lpImage = (android.widget.LinearLayout.LayoutParams) contentImage.getLayoutParams();
		System.out.println("refresh"+lpImage.height+"-------------"+lpImage.width);
		lpImage.bottomMargin += 10;
		
		Anim.refresh(getContext(), contentImage);
	}
	
	@Override
	public long getLastRefresh() {
		return lastRefresh;
	}

	@Override
	public void setLastRefresh(long lastRefresh) {
		this.lastRefresh = lastRefresh;
	}


	private boolean emptyList(){
		if(viewHasHeader && viewHasFooter) return view.getCount() == 2;
		return view.getCount() == 1;
	}


	@Override
	public void footerShow() {
		LinearLayout.LayoutParams lpCenter = (android.widget.LinearLayout.LayoutParams) footerContent.getLayoutParams();
		lpCenter.topMargin=5;
		footerContent.setLayoutParams(lpCenter);
		ImageView contentImage = (ImageView)footer.findViewById(FOOTER_IMAGE_ID);

		Anim.refreshMiddle(getContext(), contentImage);
	}


	@Override
	public void footerHiden() {
		LinearLayout.LayoutParams lpCenter = (android.widget.LinearLayout.LayoutParams) footerContent.getLayoutParams();
		lpCenter.topMargin=OFFSET;
		if(headerContent != null){
			headerContent.setLayoutParams(lpCenter);
		}
		ImageView contentImage = (ImageView)footer.findViewById(FOOTER_IMAGE_ID);
		LinearLayout.LayoutParams lpImage = (android.widget.LinearLayout.LayoutParams) contentImage.getLayoutParams();
	}
	
	private boolean checkCanScroll(){
		boolean emptyList = emptyList();
		int firstVisiblePosition =  this.getView().getFirstVisiblePosition();
		return refreshing|| firstVisiblePosition != 0 || emptyList(); 
	}

}
