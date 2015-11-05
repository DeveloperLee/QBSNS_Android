package com.thinksns.adapter;
import java.util.HashMap;
import java.util.Map;

import com.thinksns.android.R;
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
import com.thinksns.unit.ListViewAppend;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * 用户获取当前社区内所有用户微博的适配器
 * @author Administrator
 *
 */
public class PublicWeiboListAdapter extends SociaxListAdapter {
	

	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private static ListViewAppend append;
	private Weibo weibo;
	private Activity activityContent;

	@Override
	public Weibo getFirst() {
		// TODO Auto-generated method stub
		return (Weibo) super.getFirst();
	}

	@Override
	public Weibo getLast() {
		// TODO Auto-generated method stub
		return (Weibo) super.getLast();
	}

	@Override
	public Weibo getItem(int position) {
		// TODO Auto-generated method stub
		return (Weibo) super.getItem(position);
	}

	public PublicWeiboListAdapter(ThinksnsAbscractActivity context, ListData<SociaxItem> data) {
		super(context,data);
		append = new ListViewAppend(context); 
	}
	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View rowView = this.viewMap.get(position);
		 WeiboDataItem weiboDataItem;
		 if(rowView == null){
			 weiboDataItem = new WeiboDataItem();
			 rowView = this.inflater.inflate(R.layout.weibolist, null);
			 weibo = (Weibo)this.getItem(position);
			 boolean canRefresh = false;
			if(refresh == null){
				canRefresh = true;
			}else{
				canRefresh = refresh.isClickable();
			}
			if(position == this.list.size()-1 && canRefresh){
				doRefreshFooter();
			}
			append.appendWeiboData((Weibo)this.getItem(position), rowView,weiboDataItem);
			viewMap.put(position, rowView);
		 }else{
			 weiboDataItem = (WeiboDataItem)rowView.getTag();
		 }
		return rowView;
	}
*/
    /**
     * 重载适配器核心方法，为ListView添加Weibo信息
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = this.inflater.inflate(R.layout.weibolist, null);
		}
		weibo = this.getItem(position);
		boolean canRefresh = false;
		if(refresh == null){
			canRefresh = true;
		}else{
			canRefresh = refresh.isClickable();
		}
		if(position == this.list.size()-1 && canRefresh && position >=10){
			doRefreshFooter();
		}
		//Weibo weibo = this.getItem(position);
		//获取目标位置上的Weibo对象然后传入ListViewAppend对象进行解析和配置布局
		append.appendWeiboData(weibo, convertView);
		return convertView;
	}
	@Override
	public void doRefreshHeader() {
		// TODO Auto-generated method stub
		super.doRefreshHeader();
	}

	@Override
	public ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException {
		ListData<SociaxItem>  weiboDatas = getApiStatuses().publicHeaderTimeline((Weibo)obj, PAGE_COUNT);
		return weiboDatas;
	}



	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		return getApiStatuses().publicFooterTimeline((Weibo)obj, PAGE_COUNT);
	}



	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem>  weiboDatas = getApiStatuses().publicTimeline(count);
		return weiboDatas;
	}
	
	
    /**
     * 重写isEnabled方法目的是让已经被系统管理员删除的Weibo item
     * 在ListView中不可点击,每次绘制item都会自动调用这个方法来检查
     * 这个item是否可以点击。
     */
	@Override
	public boolean isEnabled(int position) {
		if(this.getItem(position).isDelByAdmin()){
			return false;
		}
		return super.isEnabled(position);
	}

	private ApiStatuses getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getStatuses();
	}

}
