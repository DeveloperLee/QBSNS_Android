package com.thinksns.components;

import com.thinksns.android.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义登陆页面dialog窗口
 * @author lizihao
 *
 */
public class SimpleDialog extends Dialog{
	
	private TextView message;
	
	
	public SimpleDialog(Context context,String content){
		super(context,R.style.simple_dialog);
		initDialog(context, content);
	}
    
	/**
	 * 必须用inflate才能对内部组件textview进行操作
	 * @param context
	 * @param content
	 */
	private void initDialog(Context context,String content){
//		LinearLayout dialogLayout = (LinearLayout) View.inflate(context, 
//				R.layout.simple_dialog_layout, null);
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.simple_dialog_layout, null);
		this.setContentView(dialogLayout);
		message = (TextView) dialogLayout.findViewById(R.id.message);
		message.setText(content);
	}

}
