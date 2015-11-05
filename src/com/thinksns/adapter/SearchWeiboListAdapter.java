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
import com.thinksns.model.Weibo; 

public class SearchWeiboListAdapter extends WeiboListAdapter {
	private String key;

	public SearchWeiboListAdapter(ThinksnsAbscractActivity context,ListData<SociaxItem> data,String key){
		super(context,data);
		this.key = key;
	}

	public SearchWeiboListAdapter(ThinksnsAbscractActivity context,
			ListData<SociaxItem> data) {
		super(context, data);
	}
	


	@Override
	public ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException {
		return getApiStatuses().searchHeader(this.key,(Weibo)obj, 20);
	}



	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		return getApiStatuses().searchFooter(this.key,(Weibo)obj, 20);
	}



	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return getApiStatuses().search(this.key,count);
	}
	
	private ApiStatuses getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getStatuses();
	}
	

}
