package com.thinksns.adapter;

import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiUsers;
import com.thinksns.components.LoadingView;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;
import com.thinksns.unit.Anim;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;


/**
 * UserListAdapter PublicWeiboList FollowListAdapter的基类
 * 用于实现自定义ListAdapter以填充ListView样式。
 * @author Administrator
 *
 */
public abstract class SociaxListAdapter extends BaseAdapter {
	private static final String TAG = "SociaxListAdapter";
	protected ListData<SociaxItem> list;
	protected ThinksnsAbscractActivity context;
	protected LayoutInflater inflater;
	public static final int LIST_FIRST_POSITION = 0;
	protected static View refresh;
	protected static Worker thread;
	protected ActivityHandler handler;
	protected ResultHandler resultHander;
	protected static String Type;
	public static final int REFRESH_HEADER = 0;
	public static final int REFRESH_FOOTER = 1;
	public static final int REFRESH_NEW    = 2;
	public static final int REFRESH_SEARCH = 3;
	
	public static final int PAGE_COUNT = 20;
	private static LoadingView loadingView;
	
	public String isRefreshActivity;
	
	public SociaxListAdapter (ThinksnsAbscractActivity context,ListData<SociaxItem> list){
		this.list = list;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		if(isRefreshActivity !=null && !isRefreshActivity.equals("ThinksnsWeiboContentList"))
			refresh = this.context.getCustomTitle().getRight();
		
		if(list.size() == 0){
			SociaxListAdapter.thread = new Worker((Thinksns)context.getApplicationContext(),Type+" Refresh");
			handler = new ActivityHandler(thread.getLooper(),context);
			resultHander = new ResultHandler();
		}
	}
	

	public abstract ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException;
	
	public abstract ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException;
	
	public abstract ListData<SociaxItem>  refreshNew(int count) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException;

	public ListData<SociaxItem> searchNew (Object o)throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException{
		return null ;
	}
		
	
	
	/**
	 * 提供给外部Activity类的封装方法，当此方法被调用的时候
	 * SociaxListAdapter（或者其实现类）调用内部refreshNewWeiboList()方法
	 * 其余见refreshNewWeiboList()方法
	 */
	public void loadInitData(){
		if(!((Thinksns)this.context.getApplicationContext()).isNetWorkOn()){
			Toast.makeText(context,
					"网络设置不正确，请设置网络",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(this.getCount() == 0){
			ListData<SociaxItem> cache = Thinksns.getLastWeiboList();
			if(cache != null){
				this.addHeader(cache);
			}else{
				if(loadingView != null){
				loadingView = (LoadingView)context.findViewById(LoadingView.ID);
				loadingView.show((View)context.getListView());}
				refreshNewWeiboList();
			}
		}
	}
	
	public Context getContext(){
		return this.context;
	}
	
	
	public SociaxItem getFirst(){
		return this.list.size()==0 ? null : this.list.get(LIST_FIRST_POSITION);
	}
	
	public SociaxItem getLast(){
		return this.list.get(this.list.size()-1);
	}
	
	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public SociaxItem getItem(int position) {
		return this.list.get(position);
	}

	public void addHeader(ListData<SociaxItem> list) {
		if (null != list) {
			if (list.size() > 0) {
				for (int i = 1; i <= list.size(); i++) {
					this.list.add(0, list.get(list.size() - i));
				}
				this.notifyDataSetChanged();
				if(isRefreshActivity ==null || !isRefreshActivity.equals("ThinksnsWeiboContentList"))
				Toast.makeText(context,
						com.thinksns.android.R.string.refresh_success,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context,
						com.thinksns.android.R.string.refresh_error,
						Toast.LENGTH_SHORT).show();
			}

			/*
			 * for(SociaxItem item:list){ this.list.add(0, item); }
			 */
			// 修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新ListView
			this.notifyDataSetChanged();
		}
	}
	
	public void changeListDataNew(ListData<SociaxItem> list){
		if (null != list) {
			if (list.size() > 0) {
				//hasRefreshFootData = true ;
				this.list= list;
				//lastNum = this.list.size();
				this.notifyDataSetChanged();
				
			}else{
				this.list.clear();
				this.notifyDataSetChanged();
			}
		}

		if(list == null || list.size()== 0 || list.size()< 20 ){
			//context.getListView().hideFooterView();
		}
		if(this.list.size() == 0){
			Toast.makeText(context,R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void addFooter(ListData<SociaxItem> list) {
		if (null != list) {
			if (list.size() > 0) {
				this.list.addAll(list);
				this.notifyDataSetChanged();
			}
		}
	}
	
	public void deleteItem(int position){
			this.list.remove(position-1);
			this.notifyDataSetChanged();
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	/**
	 * 头部刷新   
	 */
	public void doRefreshHeader(){
		if(!((Thinksns)this.context.getApplicationContext()).isNetWorkOn()){
			Toast.makeText(context,
					"网络设置不正确，请设置网络",
					Toast.LENGTH_SHORT).show();
			context.getListView().headerHiden();
			return;
		}
		SociaxListAdapter.thread = new Worker((Thinksns)context.getApplicationContext(),Type+" Refresh");
		handler = new ActivityHandler(thread.getLooper(),context);
		resultHander = new ResultHandler();
		
		//得到头部右部分
		
		refresh = this.context.getCustomTitle().getRight();
		
		if(refresh != null){
			refresh.clearAnimation();
			//设置头部右边刷新
			//Anim.refreshMiddle(context, refresh);
			if(null!=isRefreshActivity && isRefreshActivity.equals("ThinksnsMyWeibo"))
				Anim.refresh(context, refresh,com.thinksns.android.R.drawable.spinner_black_60);
			if(null!=isRefreshActivity && isRefreshActivity.equals("ThinksnsTopicActivity"))
				Anim.refresh(context, refresh,com.thinksns.android.R.drawable.spinner_black_60);
			refresh.setClickable(false);
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
		if(isRefreshActivity ==null || !isRefreshActivity.equals("ThinksnsWeiboContentList")){
			context.getListView().headerRefresh();
			context.getListView().headerShow();
		}
    	Message msg = handler.obtainMessage();
    	msg.obj = this.getFirst();
    	msg.what = REFRESH_HEADER;
    	handler.sendMessage(msg);
	}
	
	/**
	 * 当微博列表拉到当前最下端的时候触发的方法
	 * 向服务器发送获取余下微博的request，如果没有服务器返回空
	 * 如果有服务器继续向list内填充数据以保证可以浏览而更多微博。
	 */
	public void doRefreshFooter(){
		if(!((Thinksns)this.context.getApplicationContext()).isNetWorkOn()){
			Toast.makeText(context,
					"网络设置不正确，请设置网络",
					Toast.LENGTH_SHORT).show();
			return ;
		}
		SociaxListAdapter.thread = new Worker((Thinksns)context.getApplicationContext(),Type+" Refresh");
		handler = new ActivityHandler(thread.getLooper(),context);
		resultHander = new ResultHandler();
		//context.getListView().footerShow();
		if(refresh != null){
			//Anim.refreshMiddle(context, refresh);
			refresh.setClickable(false);
		}
    	Message msg = handler.obtainMessage();
    	msg.obj = this.getLast();
    	msg.what = REFRESH_FOOTER;
    	handler.sendMessage(msg);
	}

	
	protected void cacheHeaderPageCount(){
		ListData<SociaxItem> cache = new ListData<SociaxItem>();
		for(int i=0;i<PAGE_COUNT;i++){
			cache.add(0,this.list.get(i));
		}
		Thinksns.setLastWeiboList(cache);
	}
	
	/**
	 *启动handler，将message的what参数设为REFRESH_NEW(刷新微博)
	 *传递到ActivityHandler类中通过handleMassage()函数来处理。
	 */
	protected void refreshNewWeiboList(){
		if(refresh != null){
			//设置加载适配器的时候头部右边的动画
			//Anim.refreshMiddle(context, refresh);
			if(null!=isRefreshActivity && isRefreshActivity.equals("ThinksnsMyWeibo"))
				Anim.refresh(context, refresh,com.thinksns.android.R.drawable.spinner_black_60);
			refresh.setClickable(false);
		}
		Message msg = handler.obtainMessage();
    	msg.what = REFRESH_NEW;
    	handler.sendMessage(msg);
	}
	
	
	public void doSearchNew(Object o){
		Message msg = handler.obtainMessage();
    	msg.what = REFRESH_SEARCH;
    	msg.obj = o;
    	handler.sendMessage(msg);
	}
	
	
	/**
	 * 处理由refreshWeiboList()等方法传来的message
	 * 并继续向ResultHandler传输包含操作结果的message.
	 * @author Lizihao
	 *
	 */
	private  class ActivityHandler extends Handler {
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ListData<SociaxItem> newData = null;
			Message mainMsg = new Message();
			mainMsg.what = ResultHandler.ERROR;
			try {
				switch(msg.what){
				case REFRESH_HEADER:
					newData = refreshHeader((SociaxItem)msg.obj);
					Log.d("SociaxListAdapter" , "do refreshHeader" );
					break;
				case REFRESH_FOOTER:
					newData =refreshFooter((SociaxItem)msg.obj);
					Log.d("SociaxListAdapter" , "do refreshFooter" );
					break;
				case REFRESH_NEW:
					newData =refreshNew( PAGE_COUNT );
					break;
				case REFRESH_SEARCH:
					newData =searchNew( msg.obj );
					break;
				}
				mainMsg.what = ResultHandler.SUCCESS;
				mainMsg.obj  = newData;
				mainMsg.arg1 = msg.what;
			} catch (VerifyErrorException e) {
				mainMsg.obj = e.getMessage();
			} catch (ApiException e) {
				mainMsg.obj = e.getMessage();
			} catch (ListAreEmptyException e) {
				mainMsg.obj = e.getMessage();
			} catch (DataInvalidException e) {
				mainMsg.obj = e.getMessage();
			} 
			resultHander.sendMessage(mainMsg);
		}
	}
	
	private class ResultHandler extends Handler{
		private static final int SUCCESS = 0;
		private static final int ERROR   = 1;
		
		public ResultHandler() {
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			context.getListView().setLastRefresh(System.currentTimeMillis());
			if(msg.what == SUCCESS){
				switch (msg.arg1){
				case REFRESH_NEW:
					addFooter((ListData<SociaxItem>)msg.obj);
					break;
				case REFRESH_HEADER:
					addHeader((ListData<SociaxItem>)msg.obj);
					context.getListView().headerHiden();
					break;
				case REFRESH_FOOTER:
					addFooter((ListData<SociaxItem>)msg.obj);
					context.getListView().footerHiden();
					break;
				case REFRESH_SEARCH:
					changeListDataNew((ListData<SociaxItem>)msg.obj);
					context.getListView().footerHiden();
					break;
				}
			}else{
				if(isRefreshActivity ==null || !isRefreshActivity.equals("ThinksnsWeiboContentList")){
					Toast.makeText(context,(String) msg.obj, Toast.LENGTH_SHORT).show();
					context.getListView().headerHiden();
					context.getListView().footerHiden();
				}
			}
			if(loadingView != null) 
				loadingView.hide((View)context.getListView());
			if(refresh != null)
				cleanRightButtonAnim(refresh);
		}
	}
	/**
	 * 清除动画
	 * @param v
	 */
	protected void cleanRightButtonAnim(View v){
		v.setClickable(true);
		v.setBackgroundResource(context.getCustomTitle().getRightResource());
		v.clearAnimation();
	}

	
	
	
	public int getMyUid(){
		Thinksns app = thread.getApp();
		return Thinksns.getMy().getUid();
	}
	
	public ApiUsers getApiUsers(){
		Thinksns app = thread.getApp();
		return  app.getUsers();		
	}

	
	public int getMySite(){
		Thinksns app = thread.getApp();
		if(Thinksns.getMySite()==null){
			return 0;
		}else{
			return Thinksns.getMySite().getSite_id();
		}
	}
	
}
