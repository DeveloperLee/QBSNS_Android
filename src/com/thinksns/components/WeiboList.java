package com.thinksns.components;

import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsWeiboContentList;
import com.thinksns.model.Weibo;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class WeiboList extends SociaxList{

	public WeiboList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WeiboList(Context context) { 
		super(context);
	}

	@Override
	protected void onClick(View view,int position,long id) {
		System.out.println("WeiboList"+position);
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.weibo_data);
		Weibo weiboData = (Weibo)layout.getTag();
		Bundle data = new Bundle();
		if(weiboData.getTempJsonString() != null){
			data.putString("data", weiboData.getTempJsonString());
		}else{
			data.putString("data", weiboData.toJSON());
		}
		data.putInt("position", getLastPosition());
		data.putInt("this_position", position);
		Thinksns app = (Thinksns)getContext().getApplicationContext();
		app.startActivity(getActivityObj(), ThinksnsWeiboContentList.class,data);
	}
}
