package com.thinksns.adapter;

import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiStatuses;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.ListData;
import com.thinksns.model.NotifyCount;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.Weibo; 

public class AtomAdapter extends WeiboListAdapter {

	public AtomAdapter(ThinksnsAbscractActivity context,
			ListData<SociaxItem> data) {
		super(context, data);
	}
	

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem> weiboDatas = this.getApiStatuses().mentionsHeader(
				(Weibo) obj, PAGE_COUNT);
		this.getApiUsers().unsetNotificationCount(NotifyCount.Type.atMe,
				getMyUid());
		Thinksns app = (Thinksns) this.context.getApplicationContext();
		app.getWeiboSql().deleteWeibo(weiboDatas.size());
		if (weiboDatas.size() > 0) {
			for (int i = 0; i < weiboDatas.size(); i++) {
				app.getAtMeSql().addAtMe((Weibo) weiboDatas.get(i),
						getMySite(), getMyUid());
			}
		}
		return weiboDatas;
	}


	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return this.getApiStatuses().mentionsFooter((Weibo)obj, PAGE_COUNT);
	}


	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem>  weiboDatas = this.getApiStatuses().mentions(count);
		this.getApiUsers().unsetNotificationCount(NotifyCount.Type.atMe,
				getMyUid());
		Thinksns app = (Thinksns)this.context.getApplicationContext();
		if(this.list.size() == 0){
			for(int i=0;i<weiboDatas.size();i++){
				app.getAtMeSql().addAtMe((Weibo)weiboDatas.get(i),getMySite(),getMyUid());
			}			
		}
		return weiboDatas;
	}
	
	private ApiStatuses getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getStatuses();
	}


}
