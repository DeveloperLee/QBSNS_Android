package com.thinksns.listener;

public interface OnTouchListListener {
	public void headerShow();
	public void headerHiden();
	public void headerRefresh();
	public long getLastRefresh();
	public void setLastRefresh(long lastRefresh);
	public void footerShow();
	public void footerHiden();
} 
