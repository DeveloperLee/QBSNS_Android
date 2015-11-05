package com.thinksns.components;

import com.thinksns.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoadingView extends RelativeLayout {
	public static final int ID = 3306;

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.initOtherComponent(context);  
	}


	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initOtherComponent(context);  
	}

	public LoadingView(Context context) {
		super(context);
		this.initOtherComponent(context);  
	}
	
	public void show(View view){
		final View v = view;
		this.post(new Runnable(){
			@Override
			public void run() {
				v.setVisibility(View.GONE);
				LoadingView.this.setVisibility(View.VISIBLE);
			}
			
		});
	}
	
	public void error(String text,View view){
		final String info = text;
		final View   v = view;
		this.post(new Runnable(){

			@Override
			public void run() {
				Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show(); // TODO Auto-generated method stub
				v.setVisibility(View.GONE);
				LoadingView.this.setVisibility(View.VISIBLE);
			}
			
		});
	}
	public void hide(View view){
		final View v = view;
		this.post(new Runnable(){
			@Override
			public void run() {
				v.setVisibility(View.VISIBLE);
				LoadingView.this.setVisibility(View.GONE);
			}
		});
		
	}

	private void initOtherComponent(Context context) {
		this.setId(ID);
		View.inflate(context,R.layout.loading, this);
		this.setVisibility(View.GONE);
	}

}
