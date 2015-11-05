package com.thinksns.components;

import android.content.Context;
import android.util.AttributeSet;

import android.view.View;

public class MessageDetail extends SociaxList{
	public MessageDetail(Context context){
		super(context);
	}

	public MessageDetail(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onClick(View view, int position, long id) {
		return;
	}


}
