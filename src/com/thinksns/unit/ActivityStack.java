package com.thinksns.unit;

import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class ActivityStack extends Stack< Activity> {
	private static Stack<Activity> cacheExit;
	private static Intent intent;

	public ActivityStack() {
		super();
		intent = new Intent();
		cacheExit = new Stack<Activity>();
	}
	
	public void addCache(Activity activity){
		if(cacheExit == null){
			cacheExit = new ActivityStack();
		}
		cacheExit.push(activity);
	}

	

	public void startActivity(Activity now,Class<? extends Activity> target,Bundle data) {
		super.push(now);
		this.addCache(now);
		intent.setClass(now, target);
		if(data != null){
			if(intent.getExtras() != null){
				intent.replaceExtras(data);
			}else{
				intent.putExtras(data);
			}
		}
		now.startActivity(intent);
		Anim.in(now);
	}
	
	
	public void startActivity(Activity now,Class<? extends Activity> target) {
		this.startActivity(now, target,null);
	}
	
	public void returnActivity(Activity now,Bundle data){
		if(this.empty()){
			return;
		}
		Activity temp = this.pop();
		intent.setClass(now, temp.getClass());
		if(data != null){
			intent.putExtras(data);
		}
		now.startActivity(intent);
		Anim.in(now);
		now.finish();
	}

	
	public void returnActivity(Activity now){
		this.returnActivity(now, null);
	} 
	
	@Override
	public void clear() {
		while(!cacheExit.empty()){
			cacheExit.pop().finish();
		}
		super.clear();
	}


	@Override
	public synchronized Activity pop() {
		return super.pop();
	}
}
