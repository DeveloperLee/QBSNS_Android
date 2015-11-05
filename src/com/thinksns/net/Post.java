package com.thinksns.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.thinksns.model.Weibo;
import com.thinksns.unit.JSONHelper;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class Post extends Request{
	
	private List<NameValuePair> params;
	
	public Post(){
		super();
		params = new ArrayList<NameValuePair>();
	}
	public Post(Builder uri) {
		super(uri);
		params = new ArrayList<NameValuePair>();
	}
	
	public Post(String Url){
		super(Url);
	}


	@Override
	public Request append(String name,Object value) {
		if(JSONHelper.isArray(value.getClass()) || JSONHelper.isCollection(value.getClass())){
			this.params.add(new BasicNameValuePair(name, JSONHelper.toJSON(value)));
		}else{
			this.params.add(new BasicNameValuePair(name, value+""));
		}
		return this;
	}
	
	@Override
	protected HttpRequestBase executeObject() {
		String url;
		if(this.url != null && !this.url.equals("")){
			url = this.url;
		}else{
			Uri uriObj = uri.build();
			url = uriObj.toString();
		}
		
		Log.d(TAG, "Request with Post to" + url);
	    HttpPost httpPost = new HttpPost(url);
	    // 设置字符集 
	    HttpEntity entity;
		try {
			if(params != null){
				params.add(new BasicNameValuePair("from", Weibo.From.ANDROID.ordinal()+""));
				entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			    httpPost.setEntity(entity);
			    //清空参数
			    params.clear(); 
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(Request.TAG, "error,unsupported encoding");
		}  
	    // 设置参数实体
		return httpPost;
	}

}
