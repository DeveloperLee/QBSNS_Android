package com.thinksns.concurrent;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.client.ClientProtocolException;

import com.thinksns.exceptions.HostNotFindException;
import com.thinksns.model.ApproveSite;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.Weibo;
import com.thinksns.net.Get;
import com.thinksns.unit.ImageUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	
	private String url;
	private final WeakReference<ImageView> imageViewReference;
	public static enum Type{
		FACE,THUMB,MIDDLE_THUMB,LARGE_THUMB,LOGO
	}
	private Type type;
	
	public BitmapDownloaderTask(ImageView imageView,Type type){
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.type = type;
	}
	@Override
	protected Bitmap doInBackground(String... params) {
			try {
				Bitmap result = downloadBitmap(params[0]);
				return result;
			} catch (HostNotFindException e) {
				return null;
			}catch(Exception e){
				return null;
			}
	}
	
	private Bitmap downloadBitmap(String url) throws HostNotFindException, ClientProtocolException, IOException{
		Get get = new Get(url);
		return get.download();
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		if(this.isCancelled()){
			result = null;
		}
		if(imageViewReference != null  && result != null){
			ImageView imageView = imageViewReference.get();
			if(imageView != null){
				switch(this.type){
				case FACE:
					SociaxItem temp = (SociaxItem)imageView.getTag();
					/*if(result ==null){
						break;
					}*/
					result = ImageUtil.toRoundCorner(result, 0);
					//temp.setHeader(result);
					break;
				case THUMB:
					Weibo temp2 = (Weibo)imageView.getTag();

					temp2.setThumb(result);
					break;
				case MIDDLE_THUMB:
					Weibo temp3 = (Weibo)imageView.getTag();
					temp3.setThumbMiddle(result);
					break;
				case LARGE_THUMB:
					Weibo temp4 = (Weibo)imageView.getTag();
					temp4.setThumbLarge(result);
					break;
				case LOGO:
					ApproveSite site = (ApproveSite)imageView.getTag();
					/*if(result ==null){
						break;
					}*/ 
					result = ImageUtil.toRoundCorner(result, 0);
					site.setLogoUrl(result);
					break;
				}

				imageView.setImageBitmap(result);
			}
		}
	}

}
