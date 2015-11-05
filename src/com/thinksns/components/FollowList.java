package com.thinksns.components;

import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsUserInfo;
import com.thinksns.model.User;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class FollowList extends SociaxList{
	private  Context context;
	public FollowList(Context context){
		super(context);
		this.context = context;
	}

	public FollowList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onClick(View view, int position, long id) {
		Thinksns app = (Thinksns)getContext().getApplicationContext();
		User user = (User)this.getItemAtPosition(position);
		Bundle bundle = new Bundle();
		bundle.putInt("uid", user.getUid());
		app.startActivity(getActivityObj(), ThinksnsUserInfo.class, bundle);
	}
}
