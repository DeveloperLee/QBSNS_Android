package com.thinksns.android;

import com.mobclick.android.MobclickAgent;
import com.thinksns.api.Api;
import com.thinksns.com.data.UserSqlHelper;
import com.thinksns.components.SimpleDialog;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.User;
import com.thinksns.unit.Anim;
import com.thinksns.unit.TSUIUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ThinksnsLoginActivity extends Activity {

	private static final String TAG = "Login Activity";
	
	private static EditText username;
	private static EditText password;
	private static Worker thread = null;
	private static DialogHandler dialogHandler = null;
	protected static ActivityHandler handler = null;
	// protected static SinaHandler sinaHandler = null;
	// protected static SinaResultHandler resultHandler = null;
	private static ImageButton loginButton;
//	private static ImageButton sinaButton;
//	private static ImageButton register;
	//private static TextView site_opt;
	private static TextView textView;
	private static String myUrl = null;

//	private SmallDialog loginDialog;
//	private SmallDialog sinaDialog;
//	private SmallDialog regDialog;
	
	private SimpleDialog loginDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		MobclickAgent.onError(this);

		password = (EditText) this.findViewById(R.id.password);
		username = (EditText) this.findViewById(R.id.email);
//		register = (ImageButton) this.findViewById(R.id.reg);
		loginButton = (ImageButton) this.findViewById(R.id.login);
//		sinaButton = (ImageButton) this.findViewById(R.id.sina);
		textView = (TextView) this.findViewById(R.id.site_used_name);

		thread = new Worker((Thinksns) this.getApplicationContext(),
				"Auth User");
		handler = new ActivityHandler(thread.getLooper(), this);
		dialogHandler = new DialogHandler();

//		this.checkIntent();
		
		//Modified by lizihao 
		//this.isSupportSina();
		//this.isSupportReg();

		Intent intent = getIntent();
		Thinksns app = (Thinksns) this.getApplicationContext();
		String siteName = Thinksns.getMySite() == null ? "东软社区" : Thinksns
				.getMySite().getName();
		textView.setText(siteName);

		if (Thinksns.getMySite() == null) {
			String[] configHttp = ThinksnsLoginActivity.this.getBaseContext()
					.getResources().getStringArray(R.array.Http);
			myUrl = configHttp[0] + configHttp[1];
		} else {
			String[] tempUrl = Thinksns.dealUrl(Thinksns.getMySite().getUrl());
			myUrl = tempUrl[0] + tempUrl[1];
		}
		
		
		/*
		 * if(intent.hasExtra("oauth_token")){ SmallDialog dialog = new
		 * SmallDialog(ThinksnsLoginActivity.this,"请稍后",-0.08f); dialogHandler =
		 * new DialogHandler(dialog); dialog.setCanceledOnTouchOutside(false);
		 * Bundle data = new Bundle();
		 * data.putString("oauth_token",intent.getExtras
		 * ().getString("oauth_token") ); data.putString("oauth_token_secret",
		 * intent.getExtras().getString("oauth_token_secret")); //
		 * data.putInt("uid",new Integer(intent.getExtras().getString("uid")));
		 * data.putInt("uid",226); data.putString("loginway", "sina"); Message
		 * msg = handler.obtainMessage(); msg.setData(data);
		 * handler.sendMessage(msg); }
		 */
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginDialog = new SimpleDialog(ThinksnsLoginActivity.this, 
						"验证中 请稍后...");
				loginDialog.setCanceledOnTouchOutside(false);
				loginDialog.show();
				Bundle data = new Bundle();
				data.putString("username", username.getText().toString());
				data.putString("password", password.getText().toString());
				Message msg = handler.obtainMessage();
				msg.what = AUTH_DOWN;
				msg.setData(data);
				handler.sendMessage(msg);
			}
		});
		
		
		/*
		 * password.setOnKeyListener(new OnKeyListener(){
		 * 
		 * @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
		 * if(event != null && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode ==
		 * KeyEvent.KEYCODE_ENVELOPE) && event.getRepeatCount() == 0 &&
		 * event.getAction() == KeyEvent.ACTION_UP){ SmallDialog dialog = new
		 * SmallDialog(ThinksnsLoginActivity.this,"验证中,请稍后",-0.08f);
		 * dialogHandler = new DialogHandler(dialog);
		 * dialog.setCanceledOnTouchOutside(false); dialog.show(); Bundle data =
		 * new Bundle(); data.putString("username",
		 * username.getText().toString()); data.putString("password",
		 * password.getText().toString()); Message msg =
		 * handler.obtainMessage(); msg.setData(data); handler.sendMessage(msg);
		 * return false; } return false; } });
		 */

//		sinaButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				if (Api.mPort == null) {
//					String url = "http://"
//							+ myUrl
//							+ "/index.php?app=home&mod=Widget&act=addonsRequest&addon=Login&hook=login_on_client&type=sina";
//					intent.putExtra("url", url);
//				} else {
//					String url = "http://"
//							+ myUrl
//							+ ":"
//							+ Api.mPort
//							+ "/index.php?app=home&mod=Widget&act=addonsRequest&addon=Login&hook=login_on_client&type=sina";
//					intent.putExtra("url", url);
//				}
//				intent.setClass(ThinksnsLoginActivity.this,
//						ThinksnsStartWeb.class);
//				ThinksnsLoginActivity.this.startActivity(intent);
//				ThinksnsLoginActivity.this.finish();
//			}
//
//		});
//
//		register.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				if (Api.mPort == null) {
//					String url = "http://" 
//							+ myUrl
//							+ "/index.php?app=wap&mod=Public&act=register";
//					intent.putExtra("url", url);
//				} else {
//					String url = "http://" 
//							+ myUrl + ":" 
//							+ Api.mPort
//							+ "/index.php?app=wap&mod=Public&act=register";
//					intent.putExtra("url", url);
//				}
//				intent.setClass(ThinksnsLoginActivity.this,
//						ThinksnsStartWeb.class);
//				ThinksnsLoginActivity.this.startActivity(intent);
//				ThinksnsLoginActivity.this.finish();
//			}
//		});
//		
//		//modify by zhaowy 2012-10-22
//		register.setVisibility(View.INVISIBLE);
//        sinaButton.setVisibility(View.INVISIBLE);

	}

	/*private void isSupportSina() {
		sinaDialog = new SmallDialog(ThinksnsLoginActivity.this, "检查中", -0.08f);
		sinaDialog.setCanceledOnTouchOutside(false);
		sinaDialog.show();
		Log.d(TSCons.APP_TAG, "Login issuport sina dialog show ");
		Message msg = handler.obtainMessage();
		msg.what = SINA_LOGIN;
		handler.sendMessage(msg);
	}

	private void isSupportReg() {
		regDialog = new SmallDialog(ThinksnsLoginActivity.this, "检查中", -0.08f);
		regDialog.setCanceledOnTouchOutside(false);
		regDialog.show();
		Message msg = handler.obtainMessage();
		msg.what = FOR_REG;
		handler.sendMessage(msg);
	}*/

	// private class SinaHandler extends Handler{
	// private boolean isSupport = false;
	// private Context sinaContext = null;
	// public SinaHandler(Looper looper,Context context) {
	// super(looper);
	// this.sinaContext = context;
	// }
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// Thinksns app = (Thinksns)this.sinaContext.getApplicationContext();
	// Message sinaMsg = new Message();
	// try {
	// isSupport = app.getSites().isSupport();
	// resultHandler = new SinaResultHandler();
	// sinaMsg.obj = sinaButton;
	// sinaMsg.arg1 = SinaResultHandler.SURRCESS;
	// } catch (VerifyErrorException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ApiException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ListAreEmptyException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (DataInvalidException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// resultHandler.sendMessage(sinaMsg);
	// }
	//
	// }

	// private class SinaResultHandler extends Handler{
	// private final static int SURRCESS =0;
	// private final static int ERROR = 1;
	//
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// if(msg.arg1 == SURRCESS){
	// if(msg.obj.equals(false))
	// sinaButton.setVisibility(View.GONE);
	// }
	// }
	//
	// }
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.e("start", "start");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.e("onResume", "onResume");
		Thinksns app = (Thinksns) this.getApplicationContext();
		// app.initApi();
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

//	private void checkIntent() {
//		Intent getIntent = this.getIntent();
//		// 得到密码输入框的坐标。把弹窗得位置放在这里
//		if (!getIntent.getExtras().getBoolean("status")) {
//			SmallDialog dialog = new SmallDialog(this, getIntent.getExtras()
//					.getString("message"), -0.08f);
//			// dialog.show();
//
//		}
//	}

	public static final int AUTH_ERROR = 0;
	public static final int CLOSE_DIALOG = 1;
	public static final int AUTH_DOWN = 2;
	public static final int SINA_LOGIN = 3;
	public static final int FOR_REG = 4;
	public static final int GET_USERINFO = 5;

	public static final int SUCCESS = 15;
	public static final int FAIL = 16;

	
    /**
     * 处理认证的流程，认证成功后将用户信息存入数据库
     * @author Administrator
     *
     */
	private static final class ActivityHandler extends Handler {
		private static final long SLEEP_TIME = 2000;
		private static Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			ActivityHandler.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			Thinksns app = thread.getApp();
			Api.Oauth oauth = app.getOauth();
			Api.Users users = app.getUsers();
			Api.Sites siteLog = app.getSites();

			User authorizeResult = null;
			User loginedUser = null;

			boolean isSuport = false;
			switch (msg.what) {
			case AUTH_DOWN: {
				Message diaMsg = dialogHandler.obtainMessage();
				diaMsg.what = msg.what;
				try {
					authorizeResult = oauth.authorize(
							data.getString("username"),
							data.getString("password"));

					loginedUser = users.show(authorizeResult);
					loginedUser.setToken(authorizeResult.getToken());
					loginedUser
							.setSecretToken(authorizeResult.getSecretToken());
					Thinksns.setMy(loginedUser);  //设置当前用户 在生命周期内该用户始终可以用Thinksns.getMy()获取
					UserSqlHelper db = UserSqlHelper
							.getInstance(ActivityHandler.context);
					if (data.containsKey("loginway")) {
						db.clear();
					}
					db.addUser(loginedUser, true);
					diaMsg.arg1 = SUCCESS;
				} catch (UserDataInvalidException e1) {
					diaMsg.obj = e1.getMessage();
					diaMsg.arg1 = FAIL;
				} catch (VerifyErrorException e1) {
					diaMsg.arg1 = FAIL;
					diaMsg.obj = e1.getMessage();
				} catch (ApiException e1) {
					diaMsg.arg1 = FAIL;
					diaMsg.obj = e1.getMessage();
				} catch (DataInvalidException e) {
					diaMsg.arg1 = FAIL;
					diaMsg.obj = e.getMessage();
				}
				dialogHandler.sendMessage(diaMsg);
			}
				break;
			case SINA_LOGIN: {
				Message diaMsg = dialogHandler.obtainMessage();
				diaMsg.what = msg.what;
				try {
					isSuport = siteLog.isSupport();
					diaMsg.what = msg.what;
					diaMsg.arg1 = SUCCESS;
					diaMsg.arg2 = isSuport ? 1 : 0;

				} catch (VerifyErrorException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				} catch (ApiException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				} catch (DataInvalidException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				} catch (ListAreEmptyException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				}
				dialogHandler.sendMessage(diaMsg);
			}
				break;
			case FOR_REG: {
				Message diaMsg = dialogHandler.obtainMessage();
				diaMsg.what = msg.what;
				try {
					isSuport = siteLog.isSupportReg();
					diaMsg.what = msg.what;
					diaMsg.arg2 = isSuport ? 1 : 0;
				} catch (VerifyErrorException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				} catch (ApiException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				} catch (ListAreEmptyException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				} catch (DataInvalidException e) {
					diaMsg.obj = e.getMessage();
					diaMsg.arg1 = FAIL;
				}
				dialogHandler.sendMessage(diaMsg);
			}
				break;
			}
			// } catch (DataInvalidException e) {
			// errorMessage.obj = e.getMessage();
			// errorMessage.arg1 = DialogHandler.AUTH_ERROR;
			// dialogHandler.sendMessage(errorMessage);
			// thread.sleep(SLEEP_TIME);
			// errorStatus.arg1 = DialogHandler.CLOSE_DIALOG;
			// dialogHandler.sendMessage(errorStatus);
			// } catch (VerifyErrorException e) {
			// errorMessage.obj = e.getMessage();
			// errorMessage.arg1 = DialogHandler.AUTH_ERROR;
			// dialogHandler.sendMessage(errorMessage);
			// thread.sleep(SLEEP_TIME);
			// errorStatus.arg1 = DialogHandler.CLOSE_DIALOG;
			// dialogHandler.sendMessage(errorStatus);
			// } catch (ApiException e) {
			// errorMessage.obj = e.getMessage();
			// errorMessage.arg1 = DialogHandler.AUTH_ERROR;
			// dialogHandler.sendMessage(errorMessage);
			// thread.sleep(SLEEP_TIME);
			// errorStatus.arg1 = DialogHandler.CLOSE_DIALOG;
			// dialogHandler.sendMessage(errorStatus);
			// }
		}
	}
    
	
	/**
	 * 接收处理由ActivityHandler发出的message
	 * 以Toast的形式显示相应的信息
	 * @author Administrator
	 *
	 */
	private final class DialogHandler extends Handler {
		private SimpleDialog dialog;

		public DialogHandler() {
			
		}
        
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AUTH_ERROR:
//				dialog.setContent((String) msg.obj);
				break;
			case CLOSE_DIALOG:
				dialog.dismiss();
				password.requestFocus();
				break;
			case AUTH_DOWN:
				if (msg.arg1 == SUCCESS) {
					Intent intent = new Intent(ThinksnsLoginActivity.this,
							ThinksnsHome.class);
					ThinksnsLoginActivity.this.startActivity(intent);
					Anim.in(ThinksnsLoginActivity.this);
					loginDialog.dismiss();
					ThinksnsLoginActivity.this.finish();
				} else {
					loginDialog.dismiss();
					Toast.makeText(ThinksnsLoginActivity.this, (String) msg.obj, Toast.LENGTH_LONG)
					.show();
				}
				break;
//			case SINA_LOGIN:
//				if (msg.arg1 == SUCCESS) {
//					Log.d(TSCons.APP_TAG,
//							"Login issuport sina dialog sucess dismiss ");
//					sinaDialog.dismiss();
//					if (msg.arg2 == 0) {
//						sinaButton.setVisibility(View.GONE);
//					}
//				} else {
//					Log.d(TSCons.APP_TAG,
//							"Login issuport sina dialog fail dismiss "
//									+ msg.obj);
//					sinaDialog.setContent((String) msg.obj);
//					sinaDialog.dismiss();
//
//				}
//				break;
//			case FOR_REG:
//				regDialog.dismiss();
//				if (msg.arg2 == 0) {
//					sinaButton.setVisibility(View.GONE);
//				}
//				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TSUIUtils.dialog(ThinksnsLoginActivity.this);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}