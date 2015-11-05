package com.thinksns.adapter;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiStatuses;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException; 
import com.thinksns.model.Follow;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;


import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class FollowListAdapter extends UserListAdapter {
	private static String TAG="FollowList";
	public static final int FOLLOWER = 0;
	public static final int FOLLOWED = 1;
	
	private static ImageView userheader;
	private static TextView username;
	private static TextView lastWeibo;
	private static Button followButton;

	//private static ListViewAppend append;
	private int nowType;
	private static User user;
	private ThinksnsAbscractActivity context;
	private static final int ADD_FOLLOWED = 0;
	private static final int DEL_FOLLOWED = 1;
	@Override
	public Follow getFirst() {
		return (Follow) super.getFirst();
	}



	@Override
	public Follow getLast() {
		return (Follow) super.getLast();
	}



	@Override
	public Follow getItem(int position) {
		return (Follow) super.getItem(position);
	}


	public FollowListAdapter(ThinksnsAbscractActivity context, ListData<SociaxItem> data,int type,User user) {
		super(context,data);
		FollowListAdapter.user = user;
		nowType = type;
	}

	@Override
	public ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException {
		switch(nowType){
			case FOLLOWER:
				return getApiStatuses().followingHeader(user, (Follow)obj, PAGE_COUNT);
			case FOLLOWED:
				return getApiStatuses().followersHeader(user, (Follow)obj, PAGE_COUNT);
		}
		return null;
	}



	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		switch(nowType){
		case FOLLOWER:
			return getApiStatuses().followingFooter(user, (Follow)obj, PAGE_COUNT);
		case FOLLOWED:
			return getApiStatuses().followersFooter(user, (Follow)obj, PAGE_COUNT);
		}
		return null;
	}


    /**
     * 重写SociaxListAdapter内的refreshNew()方法
     * 更新粉丝/关注列表
     */
	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		switch(nowType){
		case FOLLOWER:
			return getApiStatuses().following(user, PAGE_COUNT);
		case FOLLOWED:
			return getApiStatuses().followers(user,PAGE_COUNT);
		}
		return null;
	}
	
	private ApiStatuses getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getStatuses();
	}
}
