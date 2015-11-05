package com.thinksns.components;

import com.thinksns.android.R;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.android.ThinksnsMessage;

import android.app.Activity;

import android.view.View;
import android.view.View.OnClickListener; 
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class OnlyCenterTitle extends CustomTitle {
	private int leftButtonResource;
	private int rightButtonResource;
	private  Activity context;
	private int button_count;
	public final static int AT_ME = 444;
	public final static int AT_COMMENT = 555;
	public final static int AT_MESSAGE = 666;
	
	private Button atButton;
	private Button messageButton;
	private Button commentButton;
	
	public OnlyCenterTitle(Activity context,int count) {
		super(context,((ThinksnsAbscractActivity)context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity)context;
		this.button_count = count;
		this.context = context;
//		leftButtonResource = activity.getLeftRes();
//		rightButtonResource = activity.getRightRes();
//		this.setListenerLeft(activity.getLeftListener());
//		this.setListenerRight(activity.getRightListener());
		this.setView(activity.getTitleCenter(), TITLE_ONLY_CENTER);
	}
	
	
	
	@Override
	public int getRightResource() {
		return rightButtonResource;
	}
	



	
	/* (non-Javadoc)
	 * @see com.thinksns.components.CustomTitle#addCenterText(java.lang.String, android.view.View.OnClickListener)
	 */
	@Override
	protected View addCenterText(String title, OnClickListener listener) {
		LinearLayout.LayoutParams lpCenter = new  LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout layout = new LinearLayout(this.context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(lpCenter);
		String[] buttonText = title.split(",");
		atButton = new Button(this.context);
		commentButton = new Button(this.context);
		messageButton = new Button(this.context);
		atButton.setText(buttonText[0]);
		commentButton.setPadding(0, 2, 0, 0);
		atButton.setPadding(0, 2, 0, 0);
		messageButton.setPadding(0, 2, 0, 0);
		commentButton.setText(buttonText[1]);
		messageButton.setText(buttonText[2]);
		atButton.setTextColor(this.context.getResources().getColor(R.color.white));
		commentButton.setTextColor(this.context.getResources().getColor(R.color.white));
		messageButton.setTextColor(this.context.getResources().getColor(R.color.white));
		atButton.setTextSize(14);
		commentButton.setTextSize(14);
		messageButton.setTextSize(14);
		atButton.setBackgroundResource(R.drawable.qie_nav_bg);
		commentButton.setBackgroundResource(R.drawable.qie_bg_02);
		messageButton.setBackgroundResource(R.drawable.qie_bg_03);
		atButton.setId(AT_ME);
		commentButton.setId(AT_COMMENT);
		messageButton.setId(AT_MESSAGE);
	
	/*	if(atButton)
		for(int i=0;i<buttonText.length;i++){
			Button button = new Button(this.context);
			button.setText(buttonText[i]);
			if(i==0){
				button.setId(AT_ME);
				button.setBackgroundResource(R.drawable.qie_bg_01);
			}else if(i==1){
				button.setId(AT_COMMENT);
				button.setBackgroundResource(R.drawable.qie_bg_02);
			}else{
				button.setId(AT_MESSAGE);
				button.setBackgroundResource(R.drawable.qie_bg_03);
			}
		//	button.setBackgroundResource(R.drawable.message_button);
			//button.setId(i);*/
			atButton.setOnClickListener(((ThinksnsMessage)context).getButtonListener(0,commentButton,messageButton,atButton));
			commentButton.setOnClickListener(((ThinksnsMessage)context).getButtonListener(1,atButton,messageButton,commentButton));
			messageButton.setOnClickListener(((ThinksnsMessage)context).getButtonListener(2,atButton,commentButton,messageButton));
			layout.addView(atButton);
			layout.addView(commentButton);
			layout.addView(messageButton);
			setBackground(button_count);
		
		return layout;
	}

    private void setBackground(int count){
    	switch (count) {
		case 0:
			atButton.setBackgroundResource(R.drawable.qie_nav_bg);
			commentButton.setBackgroundResource(R.drawable.qie_bg_02);
			messageButton.setBackgroundResource(R.drawable.qie_bg_03);
			break;
		case 1:
			commentButton.setBackgroundResource(R.drawable.qie_nav_bg);
			atButton.setBackgroundResource(R.drawable.qie_bg_01);
			messageButton.setBackgroundResource(R.drawable.qie_bg_03);
			break;
		case 2:
			messageButton.setBackgroundResource(R.drawable.qie_nav_bg);
			atButton.setBackgroundResource(R.drawable.qie_bg_01);
			commentButton.setBackgroundResource(R.drawable.qie_bg_02);
			break;
		default:
			break;
		}
    	
    }

	@Override
	public int getLeftResource() {
		return leftButtonResource;
	}
    
    

}
