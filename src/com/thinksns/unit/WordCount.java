package com.thinksns.unit;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class WordCount implements TextWatcher {
	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;
	private static final int MAX_COUNT = 140;
	private static TextView overWordCount;
	private static EditText text;
	
	private String tran;
	private Context context;
	public WordCount(EditText text,TextView v){
		WordCount.overWordCount = v;
		WordCount.text = text;
	}
	public WordCount(EditText text,TextView v,String tran , Context context){
		WordCount.overWordCount = v;
		WordCount.text = text;
		this.tran = tran;
		this.context = context;
		//limit(tran);
	}
	public WordCount(EditText text,TextView v,String tran){
		WordCount.overWordCount = v;
		WordCount.text = text;
		this.tran = tran;
		this.context = context;
		limit(tran);
	}
	
	public int getMaxCount(){
		return MAX_COUNT;
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;
	}

	@Override
	public void afterTextChanged(Editable s) {
		int number = MAX_COUNT-s.length();
		
		selectionStart = text.getSelectionStart();
        selectionEnd = text.getSelectionEnd();
        if(number <= 10){
        	String over = "<font color='red'>"+number+"</font>";
        	WordCount.overWordCount.setText(Html.fromHtml(over));
        }else{
        	WordCount.overWordCount.setText(""+number);
        }
        /*if (temp.length() > MAX_COUNT) {
            s.delete(selectionStart - 1, selectionEnd);
            int tempSelection = selectionStart;
            text.setText(s);
            text.setSelection(tempSelection);//设置光标在最后
		}*/
        }
	private void limit(String tran) {
		// TODO Auto-generated method stub
		int number = MAX_COUNT-tran.length();
		
        if(number <= 10){
        	String over = "<font color='red'>"+number+"</font>";
        	WordCount.overWordCount.setText(Html.fromHtml(over));
        }else{
        	WordCount.overWordCount.setText(""+number);
        }
        text.setText(tran);
        text.setSelection(0);//设置光标
	}

}
