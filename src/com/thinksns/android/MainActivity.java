package com.thinksns.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.thinksns.net.ThinksnsHttpClient;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


/**
 * 该Activity不会出现在程序中
 * @author Administrator
 *
 */
public class MainActivity extends Activity {

	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView = (TextView) findViewById(R.id.result);

		// "http://t.thinksns.com"

		findViewById(R.id.connect).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

	}

	public String httpGet(String url, String params) throws Exception {

		String result = ""; // 返回信息
		if (null != params && !params.equals("")) {
			url += "?" + params;
		}
		// 创建一个httpGet请求
		HttpGet request = new HttpGet(url);
		// 创建一个http客户端
		HttpClient httpClient = ThinksnsHttpClient.getHttpClient();
		// 接受客户端发回的响应
		HttpResponse httpResponse = httpClient.execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			// 得到客户段响应的实体内容
			HttpEntity responseHttpEntity = httpResponse.getEntity();
			// 得到输入流
			InputStream in = responseHttpEntity.getContent();
			// 得到输入流的内容
			result = getData(in);
		}
		Log.d("NetworkTest", statusCode + "");
		return result;
	}

	/**
	 * 读取返回的信息
	 * 
	 * @param in
	 * @return
	 */
	private String getData(InputStream in) {
		String result = "";
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				// result = result + line;
				sb.append(line);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
