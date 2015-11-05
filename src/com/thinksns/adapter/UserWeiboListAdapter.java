package com.thinksns.adapter;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiStatuses;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;
import com.thinksns.model.Weibo;
import com.thinksns.unit.ListViewAppend;

import android.view.View;
import android.view.ViewGroup;


public class UserWeiboListAdapter extends WeiboListAdapter {
	


	private static ListViewAppend append;
	private static User user;
	private static int uid;
	
	public UserWeiboListAdapter(ThinksnsAbscractActivity context, ListData<SociaxItem> data,int uid) {
		super(context,data);
		append = new ListViewAppend(context);
		UserWeiboListAdapter.uid = uid;
		user = new User();
		user.setUid(uid);
	}


	@Override
	public ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException {
		return getApiStatuses().userHeaderTimeline(user,(Weibo)obj, 5);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}


	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		return getApiStatuses().userFooterTimeline(user,(Weibo)obj, 5);
	}



	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return getApiStatuses().userTimeline(user,count);
	}
	
	private ApiStatuses getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getStatuses();
	}


}
