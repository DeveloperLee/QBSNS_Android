package com.thinksns.adapter;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

/**
 * 配置首页微博ListView的自定义适配器
 * @author Lizihao
 *
 */
public class WeiboListAdapter extends SociaxListAdapter {
	

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

	public WeiboListAdapter(ThinksnsAbscractActivity context, ListData<SociaxItem> data) {
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
			Toast.makeText(convertView.getContext(), "微博加载中 请稍后...", Toast.LENGTH_LONG).show();
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
		if(obj == null){
			return refreshNew(PAGE_COUNT);
		}
		ListData<SociaxItem>  weiboDatas = getApiStatuses().friendsHeaderTimeline((Weibo)obj, PAGE_COUNT);
		Thinksns app = (Thinksns)this.context.getApplicationContext();
		app.getWeiboSql().deleteWeibo(weiboDatas.size());
		if(weiboDatas.size()>0){
			for(int i=0;i<weiboDatas.size();i++){
				app.getWeiboSql().addWeibo((Weibo)weiboDatas.get(i),getMySite(),getMyUid());
			}
		}
		return weiboDatas;
	}



	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		return getApiStatuses().friendsFooterTimeline((Weibo)obj, PAGE_COUNT);
	}



	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem>  weiboDatas = getApiStatuses().friendsTimeline(count);
		Thinksns app = (Thinksns)this.context.getApplicationContext();
		if(this.list.size() == 0){
			for(int i=0;i<weiboDatas.size();i++){
				app.getWeiboSql().addWeibo((Weibo)weiboDatas.get(i),getMySite(),getMyUid());
			}			
		}
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
