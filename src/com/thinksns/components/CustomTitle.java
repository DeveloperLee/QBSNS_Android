package com.thinksns.components;

import com.thinksns.android.R;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class CustomTitle {
	private Activity context;

	public static final int TITLE_HAVE_ENDS = 0;
	public static final int TITLE_HAVE_LEFT = 1;
	public static final int TITLE_HAVE_RIGHT = 2;
	public static final int TITLE_ONLY_CENTER = 3;
	private OnClickListener listenerLeft;
	private OnClickListener listenerRight;
	private OnClickListener listenerCenter;
	private static int flag;

	
	protected View center;
	protected View left;
	protected View right;
	protected static TextView centerText;
	protected RelativeLayout layout;
	
	public CustomTitle(Activity context,boolean inTab) {

		this.context = context;
		if(!inTab){
			Window window = context.getWindow();
			window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
			layout = (RelativeLayout)context.findViewById(R.id.custom_title_layout);
		}else{
			layout = (RelativeLayout)context.getParent().findViewById(R.id.custom_title_layout);
		}
		layout.removeAllViews();
	}
	
	public void clear(){
		left.setVisibility(View.GONE);
		right.setVisibility(View.GONE);
		center.setVisibility(View.GONE);
	}
	
	public View getCenter(){
		return this.center;
	}
	
	public View getLeft(){
		return this.left;
	}
	
	public View getRight(){
		return this.right;
	}
	
	public int getFlag(){
		return flag;
	}
	
	
	protected  void setView(String text,int flag){
		RelativeLayout.LayoutParams lpCenter = new  RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lpCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		center = this.addCenterText(text,this.getListenerCenter());
		
		layout.addView(center,lpCenter);
		CustomTitle.flag=flag;
		switch(flag){
		case TITLE_HAVE_ENDS:
			left = addLeftButton();
			right = addRightButton();
			layout.addView(left,getLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
			layout.addView(right,getLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));
			break;
		case TITLE_HAVE_LEFT:
			left = addLeftButton();
			layout.addView(left,getLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
			break;
		case TITLE_HAVE_RIGHT:
			right = addRightButton();
			layout.addView(right,getLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));
			break;
		case TITLE_ONLY_CENTER:			
			break;
		}
	}
	private RelativeLayout.LayoutParams getLayoutParams(int align){
		RelativeLayout.LayoutParams lp = new  RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		//lp.rightMargin = (int) context.getResources().getDimension(R.dimen.title_margin);
		//lp.leftMargin = (int) context.getResources().getDimension(R.dimen.title_margin);
		lp.leftMargin=8;
		lp.rightMargin=8;
		lp.addRule(align);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		return lp;
	}
	
	
	protected View addCenterText(String title,OnClickListener listener){
		centerText = new TextView(this.context);
		//centerText.setTextSize(context.getResources().getDimension(R.dimen.title));
		centerText.setTextSize(20);
		centerText.setTextColor(context.getResources().getColor(R.color.white));
		centerText.setText(title);
		if(listener != null){
			centerText.setOnClickListener(listener);
		}
		return centerText;
	}
	
	public void setCenterText(String text){
		centerText.setText(text);
	}
	
	protected  View addButton(int id,OnClickListener listener){
		ImageButton button = new ImageButton(this.context);
		button.setBackgroundResource(id);
		button.setOnClickListener(listener);
		return button;
	}
	
	public void resetLeftListener(OnClickListener listener){
		left.setOnClickListener(listener);
	}
	
	public void resetRightListener(OnClickListener listener){
		right.setOnClickListener(listener);
	}
	
	public View addLeftButton(){
		return this.addButton(this.getLeftResource(),this.getListenerLeft());
	}
	
	public View addRightButton(){
		return  this.addButton(this.getRightResource(),this.getListenerRight());
	}
	public  int getRightResource(){ return 0;}
	public  int getLeftResource() {return 0;}

	public OnClickListener getListenerLeft() {
		return listenerLeft;
	}

	public void setListenerLeft(OnClickListener listenerLeft) {
		this.listenerLeft = listenerLeft;
	}

	public OnClickListener getListenerRight() {
		return listenerRight;
	}

	public void setListenerRight(OnClickListener listenerRight) {
		this.listenerRight = listenerRight;
	}

	public OnClickListener getListenerCenter() {
		return listenerCenter;
	}

	public void setListenerCenter(OnClickListener listenerCenter) {
		this.listenerCenter = listenerCenter;
	}
	/**
	 * @return the context
	 */
	public Activity getContext() {
		return context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(Activity context) {
		this.context = context;
	}

	
}
