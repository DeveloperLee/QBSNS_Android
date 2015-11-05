package com.thinksns.android;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.thinksns.components.CustomTitle;
import com.thinksns.components.LoadingView;
import com.thinksns.components.RightIsButton;
import com.thinksns.unit.AsyncImageLoader;
import com.thinksns.unit.ImageUtil;
import com.thinksns.unit.ImageZoomView;
import com.thinksns.unit.SimpleZoomListener;
import com.thinksns.unit.ZoomState;

public class ThinksnsImageView extends ThinksnsAbscractActivity {
	private ImageZoomView imageZoomView;
	private ZoomControls zoomCtrl;
	private ZoomState mZoomState;
	private Button saveButton;
	private Bitmap bitmap;
	private SimpleZoomListener mZoomListener;
	private String url;
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	private static LoadingView loadingView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyHandler myHandler = new MyHandler();
		final Message msg = myHandler.obtainMessage();

		url = getIntentData().getString("url");
		imageZoomView = (ImageZoomView) findViewById(R.id.image_data);
		zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl); 

		new Thread() {
			@Override
			public void run() {
				Drawable img = AsyncImageLoader.loadImageFromUrl(url);
				bitmap = drawableToBitmap(img);
				if (bitmap != null) {
					msg.obj = bitmap;
					msg.sendToTarget();
				}
			}
		}.start();

		mZoomState = new ZoomState();
		mZoomListener = new SimpleZoomListener();
		mZoomListener.setZoomState(mZoomState);
		imageZoomView.setZoomState(mZoomState);
		imageZoomView.setOnTouchListener(mZoomListener);
		this.setZoomCtrls();

		imageZoomView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setFullScreen();
			}
		});

		// resetZoomState();
	}

	// private void resetZoomState() {
	// mZoomState.setPanX(0.5f);
	// mZoomState.setPanY(0.5f);
	//
	// final int mWidth = bitmap.getWidth();
	// final int vWidth= imageZoomView.getWidth();
	// mZoomState.setZoom(1f);
	// mZoomState.notifyObservers();
	//
	// }

	private void setFullScreen() {
		if (zoomCtrl != null) {
			if (zoomCtrl.getVisibility() == View.VISIBLE) {
				// zoomCtrl.setVisibility(View.GONE);
				zoomCtrl.hide(); // 有过度效果
			} else if (zoomCtrl.getVisibility() == View.GONE) {
				// zoomCtrl.setVisibility(View.VISIBLE);
				zoomCtrl.show();// 有过渡效果
			}
		}
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.imageshow;
	}

	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return this.getString(R.string.imageshow);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return new RightIsButton(this, this.getString(R.string.imagesave));
	}

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return R.drawable.button_send;
	}

	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ImageUtil iu = new ImageUtil();
				String[] urlName = null;
				boolean result = false;
				urlName = url.split("/");
				try {
					result = iu.saveImage(urlName[urlName.length - 1], bitmap);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (result) {
					Toast.makeText(ThinksnsImageView.this, "保存成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ThinksnsImageView.this, "保存失败",
							Toast.LENGTH_SHORT).show();
				}
			}

		};
	}

	private void setZoomCtrls() {
		zoomCtrl.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				float z = mZoomState.getZoom() + 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}

		});
		zoomCtrl.setOnZoomOutClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() - 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}

		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	@Override
	public boolean isInTab() {
		// return this.getIntent().getBooleanExtra("tab", false);
		return false;
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			imageZoomView.setImage((Bitmap)msg.obj);
		}
	}

}
