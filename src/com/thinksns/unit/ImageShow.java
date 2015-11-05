package com.thinksns.unit;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.thinksns.android.R;
import com.thinksns.components.ImageBroder;
import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.concurrent.BitmapDownloaderTask.Type;
import com.thinksns.model.Weibo;

public class ImageShow extends WeiboDataSet {
private Drawable drawable;
private ImageBroder imageBorder;
private Weibo weibo;
	public void appendWeiboImageData(Weibo weibo, View view) {
		// TODO Auto-generated method stub
		
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.weibo_data);
		layout.setTag(weibo);
		
		//addHeader(weibo,view);
		removeViews(layout);
		if(weibo.hasImage()){
			LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(getThumbWidth(),getThumbHeight());
			lpImage.gravity = Gravity.LEFT;
			layout.addView(appendImage(weibo, view),getContentIndex(),lpImage);
		}
	}

	public Bitmap getBitmap(Weibo weibo,View view){
		imageBorder = (ImageBroder)appendImage(weibo, view);
		this.weibo = (Weibo)imageBorder.getTag();
		return this.weibo.getThumbLarge();
	}
	@Override
	public View appendTranspond(Weibo weibo, View view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void appendWeiboData(Weibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getContentIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getGravity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Bitmap getThumbCache(Weibo weibo) {
		// TODO Auto-generated method stub
		return weibo.getThumbLarge();
	}

	@Override
	protected Type getThumbType() {
		// TODO Auto-generated method stub
		return BitmapDownloaderTask.Type.LARGE_THUMB;
	}

	@Override
	protected String getThumbUrl(Weibo weibo) {
		// TODO Auto-generated method stub
		return weibo.getPicUrl();
	}

	@Override
	protected boolean hasThumbCache(Weibo weibo) {
		// TODO Auto-generated method stub
		return weibo.isNullForThumbLargeCache();
	}

	@Override
	public void setCommentCount(Weibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setCountLayout(Weibo weibo, View view) { 
		// TODO Auto-generated method stub

	}

	@Override
	public void setTranspondCount(Weibo weibo, View view) {
		// TODO Auto-generated method stub

	}

}
