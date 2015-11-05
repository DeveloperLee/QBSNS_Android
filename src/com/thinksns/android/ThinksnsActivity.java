package com.thinksns.android;

import com.thinksns.api.Api;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.unit.Anim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; 
import android.os.Looper;
import android.os.Message;
import android.view.Window;

public class ThinksnsActivity extends Activity {
    private static final String TAG = "Init Activity";
    private static ThinksnsAbscractActivity activity;
	private static Worker initThread = null;
	protected static ActivityHandler handler = null;
	protected static int INIT_OK = 0;
	protected static int AUTHING = 1;
	protected static int AUTH_DONE = 2;
	protected static int AUTH_ERROR = 3;
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
       
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.initApp();
        
    }
    
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	private void initApp() {
		initThread = new Worker((Thinksns)this.getApplicationContext());
		handler = new ActivityHandler(initThread.getLooper());
		Message msg = handler.obtainMessage(INIT_OK);
		handler.sendMessage(msg);
	}
	
	private final class ActivityHandler extends Handler{
		public ActivityHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what == INIT_OK){
				Thinksns app = (Thinksns)ThinksnsActivity.this.getApplicationContext();
				app.initApi();
				Intent intent;
				if(app.HasLoginUser()){
					intent = new Intent(ThinksnsActivity.this,ThinksnsHome.class);
				}else{
					Bundle data = new Bundle();
					try {
						Api.Status status = app.getOauth().requestEncrypKey();
						if(status == Api.Status.RESULT_ERROR){
							data.putBoolean("status", false);
							data.putString("message", ThinksnsActivity.this.getResources().getString(R.string.request_key_error));
						}else{
							data.putBoolean("status", true);
						}
					} catch (ApiException e1) {
						data.putBoolean("status", false);
						data.putString("message", e1.getMessage());
					}
					intent = new Intent(ThinksnsActivity.this,ThinksnsLoginActivity.class);
					intent.putExtras(data);
				}
				ThinksnsActivity.this.startActivity(intent);
			    Anim.in(ThinksnsActivity.this);
			    ThinksnsActivity.this.finish();
			    initThread.quit();
			}
		}
		
	}


}