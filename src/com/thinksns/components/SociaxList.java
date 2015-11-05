package com.thinksns.components;

import com.thinksns.android.R;
import com.thinksns.listener.OnTouchListListener;
import com.thinksns.unit.DragDown;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * 所有页面的ListView均继承该类，该类继承自Android.widget.ListView
 * 实现OnTouchListListener接口
 * @author Lizihao 
 *
 */
public abstract class SociaxList extends ListView implements OnTouchListListener{
	private DragDown dragdown;
	
	private static int lastPosition;
	private static Activity activityObj;
	

	public SociaxList(Context context){
		super(context);
		this.initSet(context);
	}

	public SociaxList(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initSet(context);
	}
    
	
	/**
	 * 初始化SociaxList布局配置
	 * 设置自定义滑动块，设置item分割线
	 * @param context
	 */
	private void initSet(Context context){
		this.setScrollbarFadingEnabled(true);
		//this.setCacheColorHint(Color.argb(0, 255, 255, 255));
		//this.setCacheColorHint(Color.TRANSPARENT);
		this.setCacheColorHint(0);
		//int color = context.getResources().getColor(R.color.line);
		this.setDivider(context.getResources().getDrawable(R.drawable.item_line));
		//this.setDivider(new ColorDrawable(color));
		//this.setDividerHeight(2);
		dragdown = new DragDown(context,this);
		this.initDrag(context);
	}


	protected void initDrag(Context context) {
		this.setOnTouchListener(dragdown);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		this.addHeaderView();
		this.addFooterView();
		super.setAdapter(adapter);
		this.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(dragdown.canDrag()) return;
				SociaxList.this.onClick(view,position,id);
			}
		});
		this.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==OnScrollListener.SCROLL_STATE_IDLE){
					setLastPosition(SociaxList.this.getFirstVisiblePosition());  //ListPos记录当前可见的List顶端的一行的位置
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}
	
	protected void addHeaderView(){
		super.addHeaderView(dragdown.getHeaderView());
	}
	
	protected void addFooterView(){
		super.addFooterView(dragdown.getFooterView());
	}
	
	protected abstract void onClick(View view,int position,long id);
	
	public void setAdapter(ListAdapter adapter,long lastTime,Activity obj) {
		setActivityObj(obj);
		this.setLastRefresh(lastTime);
		this.setAdapter(adapter);
	}

	@Override
	public void headerShow() {
		dragdown.headerShow();
	}

	@Override
	public void headerHiden() {
		dragdown.headerHiden();
	}

	@Override
	public void headerRefresh() {
		dragdown.headerRefresh();
	}

	@Override
	public long getLastRefresh() {
		return dragdown.getLastRefresh();
	}

	@Override
	public void setLastRefresh(long lastRefresh) {
		dragdown.setLastRefresh(lastRefresh);
	}

	@Override
	public void footerShow() {
		dragdown.footerShow();	
	}

	@Override
	public void footerHiden() {
		dragdown.footerHiden();		
	}

	public static Activity getActivityObj() {
		return activityObj;
	}

	private static void setActivityObj(Activity activityObj) {
		SociaxList.activityObj = activityObj;
	}

	public static int getLastPosition() {
		return lastPosition;
	}

	private static void setLastPosition(int lastPosition) {
		SociaxList.lastPosition = lastPosition;
	}
}
