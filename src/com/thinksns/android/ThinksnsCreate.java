package com.thinksns.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.thinksns.api.ApiStatuses;
import com.thinksns.components.CustomTitle;
import com.thinksns.components.LoadingView;
import com.thinksns.components.RightIsButton;
import com.thinksns.components.TSFaceView;
import com.thinksns.concurrent.Worker;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.UpdateException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.Weibo;
import com.thinksns.unit.Anim;
import com.thinksns.unit.AtHelper;
import com.thinksns.unit.Compress;
import com.thinksns.unit.ImageUtil;
import com.thinksns.unit.TSUIUtils;
import com.thinksns.unit.TopicHelper;
import com.thinksns.unit.WordCount;



public class ThinksnsCreate extends ThinksnsAbscractActivity {
	private static final String TAG = "WeiboCreate";
	public EditText edit;
	private static Worker thread;
	private static Handler handler;
	private static LoadingView loadingView;
	private static HashMap<String,Integer> buttonSet;
	private static ImageView camera;
	private static ImageView preview;
	private static RelativeLayout btnLayout;
	private static final int LOCATION = 1;
	private static final int CAMERA   = 0;
	private Image image;
	private static final int IO_BUFFER_SIZE = 4 * 1024;
	private  boolean hasImage = false;
	
	private ImageView ivTopic;
	private ImageView ivAt;
	private ImageView ivFace;
	
	private TopicHelper mTopicHelper;
	private AtHelper mAtHelper;
	private TSFaceView tFaceView;
	
	private String tempWeibo ;
	
	private TSFaceView.FaceAdapter mFaceAdapter = new TSFaceView.FaceAdapter(){

		@Override
		public void doAction(int paramInt, String paramString) {
			// TODO Auto-generated method stub
			 EditText localEditBlogView = ThinksnsCreate.this.edit;
			    int i = localEditBlogView.getSelectionStart();
			    int j = localEditBlogView.getSelectionStart();
			    String str1 = "[" + paramString + "]";
			    String str2 = localEditBlogView.getText().toString();
			    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
			    localSpannableStringBuilder.append(str2, 0, i);
			    localSpannableStringBuilder.append(str1);
			    localSpannableStringBuilder.append(str2, j, str2.length());
			    TSUIUtils.highlightContent(ThinksnsCreate.this, localSpannableStringBuilder);
			    localEditBlogView.setText(localSpannableStringBuilder, TextView.BufferType.SPANNABLE);
			    localEditBlogView.setSelection(i + str1.length());
			    Log.v("Tag", localEditBlogView.getText().toString());
		}
		
	};
	 
	private static final String PREF = "weibo_pref" ;
	private static final String DRAFT = "save_weibo" ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		
		mTopicHelper = new TopicHelper(this , edit);
		mAtHelper = new AtHelper(this);
	    tFaceView.setFaceAdapter(this.mFaceAdapter);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		initView();
		
		restorePrefs();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		
		//SharedPreferences sPreferences = getSharedPreferences(PREF, 0);
		//sPreferences.edit().putString(DRAFT,tempWeibo ).commit();
		
	}
	
	private void initView(){
		buttonSet = new HashMap<String,Integer>();
		Thinksns app = (Thinksns)ThinksnsCreate.this.getApplicationContext();
		edit = (EditText)findViewById(R.id.send_content);
		loadingView = (LoadingView)findViewById(LoadingView.ID);
		camera = (ImageView)findViewById(R.id.camera);
		ivTopic = (ImageView)findViewById(R.id.topic);
		ivAt = (ImageView)findViewById(R.id.at);
		ivFace = (ImageView)findViewById(R.id.face);
		tFaceView = (TSFaceView) findViewById(R.id.face_view);
		
		preview = (ImageView)findViewById(R.id.preview);
		btnLayout = (RelativeLayout)findViewById(R.id.btn_layout);
		this.setInputLimit();
		if(image == null) image = new Image();
		setBottomClick();
		checkIntentData();
		edit.clearFocus();
	}
	
	private void checkIntentData() {
		if(!getIntentData().containsKey("type")) return;
		int temp = getIntentData().getInt("type");
         if(temp>0){
        	 return;
         }
		String type = getIntentData().getString("type");
		if(type.equals("suggest")){
			edit.setText(R.string.input_text_suggest);
		}
		if(type.equals("joinTopic")){
			edit.setText(getIntentData().getString("topic"));
		}
		
		
	}
	
	String savePath;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			Bitmap btp = null;
			switch (requestCode){
			case CAMERA:
				/*Bundle dataBundle = data.getExtras();
				if(dataBundle == null){
					Uri imageUri = data.getData();
					System.out.println(imageUri.toString());
					try {
						btp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.toString());
					}
				}else{
					//btp = (Bitmap)dataBundle.get("data");
				}*/
				
				//data.putExtra(MediaStore.EXTRA_OUTPUT, btp);
				try {
					//image.setImagePath(ImageUtil.saveFile(picName , btp));
					
					btp=Compress.compressPicToBitmap(new File(image.getImagePath()));
				} catch (Exception e) {
					Log.e(TAG, "file saving..."+e.toString());
				}
				break;
			case LOCATION:
				btp = checkImage(data);
				break;
			}
			if(btp != null){
				
				btp =  Bitmap.createScaledBitmap(btp, preview.getWidth(), btnLayout.getHeight(), true);
				preview.setImageBitmap(btp);
				this.hasImage = true;				
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    private Bitmap checkImage(Intent data) {
		Bitmap bitmap = null;
		try {
			Uri originalUri = data.getData();
			String path = getRealPathFromURI(originalUri);
		
			path = path.substring(path.indexOf("/sdcard"), path.length());
			Log.d(TAG,"imagePath"+path);
			bitmap=Compress.compressPicToBitmap(new File(path));
			if (bitmap != null) {
				image.setImagePath(path);
			}
			
		} catch (Exception e) {
			Log.e("checkImage", e.getMessage());
		} finally {
			return bitmap;
		}
	}
    private Bitmap getCommentImage(String url){
    	 Bitmap bitmap = null;
         InputStream in = null;
         BufferedOutputStream out = null;
         try {
        	 
             in = new BufferedInputStream(new URL(url).openStream(),IO_BUFFER_SIZE);
             
             final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
             out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
             copy(in, out);
             out.flush();
             byte[] data = dataStream.toByteArray();
             bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
             data = null;
             return bitmap;
         } catch (IOException e) {
             e.printStackTrace();
             return null;
         }catch(OutOfMemoryError e){
			bitmap.recycle();
			bitmap=null;
			System.gc();
			return null;
         }
    }
    
    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

	private String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		String result = contentUri.toString();
		String[] proj = { MediaColumns.DATA };
		cursor = managedQuery(contentUri, proj, null, null, null);
		if(cursor == null) throw new NullPointerException("reader file field");
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			// 最后根据索引值获取图片路径
			result = cursor.getString(column_index);
			cursor.close();
		}
		return result;
    }
	

	private void setBottomClick() {
		camera.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(ThinksnsCreate.this)
					.setTitle("选择来源")
					.setItems(R.array.camera, image).show();
			}			
		});
		
		ivTopic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTopicHelper.insertTopicTips();
			}
		});
		
		ivAt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAtHelper.insertAtTips();
			}
		});
		
		ivFace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tFaceView.getVisibility() == View.GONE){
					tFaceView.setVisibility(View.VISIBLE);
					ivFace.setImageResource(R.drawable.btn_insert_keyboard);
					TSUIUtils.hideSoftKeyboard(ThinksnsCreate.this, edit);
				}else if(tFaceView.getVisibility() == View.VISIBLE){
					tFaceView.setVisibility(View.GONE);
					ivFace.setImageResource(R.drawable.btn_insert_face);
					TSUIUtils.showSoftKeyborad(ThinksnsCreate.this, edit);
				}
			}
		});
		
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tFaceView.getVisibility() == View.VISIBLE){
					tFaceView.setVisibility(View.GONE);
					ivFace.setImageResource(R.drawable.btn_insert_face);
					TSUIUtils.showSoftKeyborad(ThinksnsCreate.this, edit);
				System.out.println(" show soft key board ...");}
			}
		});
	}
	
	private class Image implements DialogInterface.OnClickListener{
		private String imagePath = "";
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case LOCATION:
				locationImage();
				break;
			case CAMERA:
				cameraImage();
				break;
			default:
				dialog.dismiss();
			}
		}
		//获取相机拍摄图片
		private void cameraImage() {
			if(!ImageUtil.isHasSdcard()){
				//Toast.makeText(this.ThinksnsCreate,"" ,T );//.show();
				Toast.makeText(ThinksnsCreate.this, "使用相机前先插入SD卡", Toast.LENGTH_LONG).show();
				return;
			}
			//启动相机
			Intent myIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			String picName = System.currentTimeMillis()+".jpg";
			try {
				String path=ImageUtil.saveFilePaht(picName);
				File file = new File(path);
				Uri uri = Uri.fromFile(file);
				image.setImagePath(path);
				myIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "file saving...");
			}
			startActivityForResult(myIntent, CAMERA);
		}

		private void locationImage() {
			Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
			getImage.addCategory(Intent.CATEGORY_OPENABLE);
			getImage.setType("image/*");
			startActivityForResult(Intent.createChooser(getImage, "选择照片"), LOCATION);
			
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}
	}


	private void setInputLimit() {
		TextView overWordCount = (TextView)findViewById(R.id.overWordCount);
		WordCount wordCount = new WordCount(edit,overWordCount);
		overWordCount.setText(wordCount.getMaxCount()+"");
		edit.addTextChangedListener(wordCount);
	}
	
	//设置发送按钮动画
	private void sendingButtonAnim(View v){
		buttonSet.put("left", v.getPaddingLeft());
		buttonSet.put("right", v.getPaddingRight());
		buttonSet.put("buttom", v.getPaddingBottom());
		buttonSet.put("top", v.getPaddingTop());
		buttonSet.put("height", v.getHeight());
		buttonSet.put("width", v.getWidth());
		buttonSet.put("text", R.string.publish);
		
		TextView view = (TextView)v;
		view.setHeight(24);
		view.setWidth(24);
		view.setText("");
		view.setClickable(false);
		Anim.refresh(this, view);
	}
	
	private void clearSendingButtonAnim(View v){
		TextView view = (TextView)v;
		view.setHeight(buttonSet.get("height"));
		view.setWidth(buttonSet.get("width"));
		view.setText(this.getString(buttonSet.get("text")));
		view.clearAnimation();
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.create_new;
	}
   
	@Override
	public OnClickListener getRightListener() {
		return  new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(edit.getText().toString().length() >140){
					//loadingView.error(getString(R.string.word_limit),edit);
					Toast.makeText(getApplicationContext(), R.string.word_limit, Toast.LENGTH_SHORT).show();
					
				}else {
				//camera.setEnabled(false);
				camera.setEnabled(false);
				getCustomTitle().getRight().setEnabled(false);
				//sendingButtonAnim(getCustomTitle().getRight());
				Thinksns app = (Thinksns)ThinksnsCreate.this.getApplicationContext();
				thread = new Worker(app,"Publish data");
				handler = new ActivityHandler(thread.getLooper(),ThinksnsCreate.this);
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg); }
			}
		};
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new RightIsButton(this, this.getString(R.string.sendMessage));
	}
	

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return R.drawable.button_send;
	}


	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return this.getString(R.string.publish);
	}
	
	

	private  final class ActivityHandler extends Handler {
		private  final long SLEEP_TIME = 2000;
		private  Context context = null;


		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			loadingView.show(edit);
			
			//获取数据
			Thinksns app = thread.getApp();
			ApiStatuses statuses = app.getStatuses();
			try {
					boolean update = false;
					String editContent;

					if(hasImage){
						editContent = edit.getText().length() == 0? "发布图片":edit.getText().toString();
					}else{
						editContent = edit.getText().toString();
					}
					Weibo weibo = new Weibo();
					weibo.setContent(editContent);
					if(hasImage){
						update = statuses.upload(weibo, new File(image.getImagePath()));
						checkSendResult(update);
					}else{
						if(editContent.length() == 0){
							loadingView.error("微博不能为空",edit);
							loadingView.hide(edit);
							//clearSendingButtonAnim(getCustomTitle().getRight());
							//getCustomTitle().getRight().clearAnimation();
							//Log.e("temp","temp"+getCustomTitle().getRight());
						//	getCustomTitle().getRight().setClickable(true);
						}else{
							update = statuses.update(weibo) >=1;
							checkSendResult(update);
						}
					}
				
			} catch (VerifyErrorException e) {
			//	clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.error(e.getMessage(),edit);
			} catch (ApiException e) {
			//	clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.error(e.getMessage(),edit);
			} catch (UpdateException e) {
				//clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.error(e.getMessage(),edit);
			}
			thread.quit();
		}
	}
	private void checkSendResult(boolean update){
		Thinksns app = thread.getApp();
		if (update) {
			super.sendFlag = true ;
			getIntentData().putString(TIPS, "发布成功");
			loadingView.error("发布成功", edit);
			ThinksnsCreate.this.finish();
		} else {
			//clearSendingButtonAnim(getCustomTitle().getRight());
			loadingView.error("发布失败", edit);
		}
	}

	private void restorePrefs() {
		SharedPreferences settings=getSharedPreferences(PREF, 0);
		//查找字符 没有的话就返回 空
		final String pref_height=settings.getString(DRAFT, "");
		if(!"".equals(pref_height)){
			
			AlertDialog.Builder builder = new AlertDialog.Builder(ThinksnsCreate.this); 
			 builder.setTitle(R.string.dialog_alert);
		        builder.setMessage(R.string.load_draft_or_not); 
		        builder.setPositiveButton(R.string.dialog_ok, 
		                new android.content.DialogInterface.OnClickListener() { 
		                    @Override 
		                    public void onClick(DialogInterface dialog, int which) { 
		                        dialog.dismiss(); 
		                        edit.setText(pref_height);
		            			replaceFace(edit, pref_height);
		            			//指定光标
		            			edit.requestFocus();
		                    } 
		                }); 
		        builder.setNeutralButton(R.string.dialog_delete, 
		                new android.content.DialogInterface.OnClickListener() { 
		                    @Override 
		                    public void onClick(DialogInterface dialog, int which) { 
		                        dialog.dismiss(); 
		                        SharedPreferences sPreferences = getSharedPreferences(PREF, 0);
		                		sPreferences.edit().putString(DRAFT,"" ).commit();
		                    } 
		                }); 
		       
		        builder.setNegativeButton(R.string.dialog_cancel, 
		                new android.content.DialogInterface.OnClickListener() { 
		                    @Override 
		                    public void onClick(DialogInterface dialog, int which) { 
		                        dialog.dismiss();
		                    } 
		                }); 
		        builder.create().show(); 
		}
	}
	
	private void replaceFace(EditText localEditBlogView , String content){
	    
	    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
	    localSpannableStringBuilder.append(content, 0, content.length());
	    TSUIUtils.highlightContent(ThinksnsCreate.this, localSpannableStringBuilder);
	    localEditBlogView.setText(localSpannableStringBuilder, TextView.BufferType.SPANNABLE);
	    localEditBlogView.setSelection(content.length());
	    Log.v("Tag", localEditBlogView.getText().toString());
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		tempWeibo = edit.getText().toString();
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			if(tFaceView.getVisibility() == View.VISIBLE){
				tFaceView.setVisibility(View.GONE);
				ivFace.setImageResource(R.drawable.btn_insert_face);
				//TSUIUtils.showSoftKeyborad(ThinksnsCreate.this, edit);
				return true ;
			}else if(tempWeibo.length() > 0 ){
			
					AlertDialog.Builder builder = new AlertDialog.Builder(ThinksnsCreate.this); 
					 builder.setTitle(R.string.dialog_alert);
				        builder.setMessage(R.string.save_draft_or_not); 
				        builder.setPositiveButton(R.string.dialog_ok, 
				                new android.content.DialogInterface.OnClickListener() { 
				                    @Override 
				                    public void onClick(DialogInterface dialog, int which) { 
				                        dialog.dismiss(); 
				                        SharedPreferences sPreferences = getSharedPreferences(PREF, 0);
				                		sPreferences.edit().putString(DRAFT,tempWeibo ).commit();
				                		ThinksnsCreate.this.finish();
				                    } 
				                }); 
				        builder.setNegativeButton(R.string.dialog_cancel, 
				                new android.content.DialogInterface.OnClickListener() { 
				                    @Override 
				                    public void onClick(DialogInterface dialog, int which) { 
				                        dialog.dismiss();
				                        ThinksnsCreate.this.finish();
				                    } 
				                }); 
				        builder.create().show(); 
				
			}else{
			return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);	
	}
	
}




