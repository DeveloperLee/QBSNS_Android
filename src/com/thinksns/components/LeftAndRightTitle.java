package com.thinksns.components;

import com.thinksns.android.ThinksnsAbscractActivity;

import android.app.Activity;

public class LeftAndRightTitle extends CustomTitle {
	private int leftButtonResource;
	private int rightButtonResource;



	public LeftAndRightTitle(Activity context) {
		super(context,((ThinksnsAbscractActivity)context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity)context;

		leftButtonResource = activity.getLeftRes();
		rightButtonResource = activity.getRightRes();
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		this.setView(activity.getTitleCenter(), TITLE_HAVE_ENDS);
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
