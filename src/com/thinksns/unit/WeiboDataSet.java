package com.thinksns.unit;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.thinksns.android.R;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.components.ImageBroder;
import com.thinksns.concurrent.BitmapDownloaderTask;
import com.thinksns.model.Weibo;

public abstract class WeiboDataSet {
	protected static final int TRANSPOND_LAYOUT = 111;
	protected static final int IMAGE_VIEW       = 222;
	protected static final int CONTENT_INDEX = 2;
	private static enum PATTERN{
		AT,TOPIC,URL
	}
	private ImageBroder imageBorder;
	private Object weibo;
	private Bitmap bitmap;
	
//	public abstract void appendWeiboData(Weibo weibo,View view,WeiboDataItem weiboDataItem);
	public abstract void appendWeiboData(Weibo weibo,View view);

	protected static ThinksnsAbscractActivity activityObj;
	
	protected abstract  int getContentIndex();
	
	protected abstract void setCountLayout(Weibo weibo,View view);
	
	public abstract void setTranspondCount(Weibo weibo,View view);
	
	public abstract void setCommentCount(Weibo weibo,View view);
	
	protected abstract int getGravity();
	
	protected abstract BitmapDownloaderTask.Type getThumbType();
	
	protected abstract boolean hasThumbCache(Weibo weibo);
	
	
	protected abstract String getThumbUrl(Weibo weibo);
	
	
	protected abstract Bitmap getThumbCache(Weibo weibo);
	
	
	protected int getThumbWidth() {
		// TODO Auto-generated method stub
		return LayoutParams.WRAP_CONTENT;
	}

	protected int getThumbHeight() {
		// TODO Auto-generated method stub
		return LayoutParams.WRAP_CONTENT;
	}
	//头像
	final protected  void addHeader(Weibo weibo, View view,ImageView header) {
		Thinksns app = (Thinksns)view.getContext().getApplicationContext();
		//header = (ImageView)view.findViewById(R.id.user_header);
		header.setTag(weibo);
		header.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.header));
		if(weibo.hasHeader()&& app.isNetWorkOn()){
			if(weibo.isNullForHeaderCache()){
				dowloaderTask(weibo.getUserface(), header,BitmapDownloaderTask.Type.FACE);
			}else{
				Bitmap cache = weibo.getHeader();
				if(cache == null){
					dowloaderTask(weibo.getUserface(), header,BitmapDownloaderTask.Type.FACE);
				}else{
					header.setImageBitmap(cache);
				}
			}
		}
	}

	final protected  void removeViews(LinearLayout layout) {
		ImageBroder image = (ImageBroder)layout.findViewById(IMAGE_VIEW);
		LinearLayout transpond = (LinearLayout)layout.findViewById(TRANSPOND_LAYOUT);
		if(image != null){
			layout.removeViewInLayout(image);
		}
		
		if(transpond != null){
			layout.removeViewInLayout(transpond);
		}
	}

	 public abstract View appendTranspond(Weibo weibo,View view);
	
	
	

	final protected  View appendImage(Weibo weibo,View view){
		ImageBroder image = new ImageBroder(view.getContext());
		image.setTag(weibo);
		image.setId(IMAGE_VIEW);
		if(hasThumbCache(weibo)){
			dowloaderTask(getThumbUrl(weibo), image,getThumbType());
		}else{
			Bitmap cache = getThumbCache(weibo);
			if(cache == null){
				dowloaderTask(getThumbUrl(weibo), image,getThumbType());
			}else{
				image.setImageBitmap(cache);
			}
		}
		return image;
	}
	
	final protected  void dowloaderTask(String url,ImageView image,BitmapDownloaderTask.Type type) {
		BitmapDownloaderTask task = new BitmapDownloaderTask(image,type);
		task.execute(url);
	}
	

}
