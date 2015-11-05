package com.thinksns.unit;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class ImageUtil {
	private static int compress = 100;
	
	public static boolean isHasSdcard()  
	{  
	    String status = Environment.getExternalStorageState();  
	    if (status.equals(Environment.MEDIA_MOUNTED)){  
	       return true;  
	    } else {  
	       return false;  
	    }  
	}  
	
	public static String getSDPath(){ 
	       File sdDir = null; 
	       boolean sdCardExist = Environment.getExternalStorageState()   
	                           .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
	       if   (sdCardExist)   
	       {                               
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
	      }   
	      return sdDir.toString()+"/"; 
	} 
	
	public static String saveFilePaht(String picName) throws FileNotFoundException{
		
		File dir = new File(getSDPath());
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		String tmpFilePath = getSDPath()+picName;
		
		/*File tmpFile = new File(tmpFilePath);
		FileOutputStream fileOut = new FileOutputStream(tmpFile);
		btp.compress(CompressFormat.JPEG, compress, fileOut);*/
		return tmpFilePath;
	}
	
	public static String saveFile(String picName,Bitmap btp) throws FileNotFoundException{
		
		File dir = new File(getSDPath());
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		String tmpFilePath = getSDPath()+picName;
		File tmpFile = new File(tmpFilePath);
		FileOutputStream fileOut = new FileOutputStream(tmpFile);
		btp.compress(CompressFormat.JPEG, compress, fileOut);
		return tmpFilePath;
	}
	
	public boolean saveImage(String picName,Bitmap bit) throws FileNotFoundException{
		File dir = new File(getSDPath()+"/thinkSNS_Image/");
		if(!dir.exists()){
			dir.mkdirs();
		}
		boolean result = false;
		String tmpFilePath = getSDPath()+"/thinkSNS_Image/"+picName;
		File tmpFile = new File(tmpFilePath);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile));   
		result = bit.compress(CompressFormat.JPEG, compress, bos);
		try {
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
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
	
	
	    
	    /**
	     * 把图片变成圆角
	     * @param bitmap 需要修改的图片
	     * @param pixels 圆角的弧度
	     * @return 圆角图片
	     */
	    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
	    
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);
	
	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	        final RectF rectF = new RectF(rect);
	        final float roundPx = pixels;
	
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);
	
	        return output;
	    }
	
	    
	   /**
	     * 使圆角功能支持BitampDrawable
	     * @param bitmapDrawable 
	     * @param pixels 
	     * @return
	     */
	    public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {
	        Bitmap bitmap = bitmapDrawable.getBitmap();
	        bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
	        return bitmapDrawable;
	    }
	
}

