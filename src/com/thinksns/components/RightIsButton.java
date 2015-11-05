package com.thinksns.components;

import com.thinksns.android.R;
import com.thinksns.android.ThinksnsAbscractActivity;

import android.app.Activity;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class RightIsButton extends CustomTitle { 




	private int leftButtonResource;
	private int rightButtonResource;
	private static String rightText;


	public RightIsButton(Activity context,String text2) {
		super(context,((ThinksnsAbscractActivity)context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity)context;

		leftButtonResource =  activity.getLeftRes();
		rightButtonResource = activity.getRightRes();
		
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		
//		leftButtonResource = R.drawable.icon_back_32;
//		rightButtonResource = R.drawable.button_send;
		rightText = text2;
		this.setView(activity.getTitleCenter(), TITLE_HAVE_ENDS);
	}
	
	/* (non-Javadoc)
	 * @see com.thinksns.components.CustomTitle#addRightButton()
	 */
	@Override
	public View addRightButton() {
		TextView button = new TextView(this.getContext());
		button.setBackgroundResource(this.getRightResource());
		button.setOnClickListener(this.getListenerRight());
		button.setTextColor(this.getContext().getResources().getColor(R.color.white));
		//button.setPadding(10, 20, 10, 2);
		button.setText(rightText);
		button.setGravity(Gravity.CENTER);
		button.setTextSize(14);
		return button;
	}
	
	
	
	
	@Override
	public int getRightResource() {
		return rightButtonResource;
	}



	   
	@Override
	public int getLeftResource() {
		return leftButtonResource;
	}



}
