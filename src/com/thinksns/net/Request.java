package com.thinksns.net;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.util.Log;

import com.thinksns.constant.TSCons;
import com.thinksns.exceptions.HostNotFindException;
import com.thinksns.unit.TSUIUtils;

public abstract class Request {
	protected static final String TAG = "HttpRequest";
	protected HttpClient httpClient;
	protected Uri.Builder uri;
	protected static String token;
	protected static String secretToken;
	protected String url;
	protected ThinksnsHttpClient thinsnsHttpClient;

	public Request() {
		thinsnsHttpClient = new ThinksnsHttpClient();
		httpClient = ThinksnsHttpClient.getHttpClient();
	}

	public Request(String url) {
		this.url = url;
		thinsnsHttpClient = new ThinksnsHttpClient();
		httpClient = ThinksnsHttpClient.getHttpClient();
	}

	public Request(Uri.Builder uri) {
		this.uri = uri;
		thinsnsHttpClient = new ThinksnsHttpClient();
		httpClient = ThinksnsHttpClient.getHttpClient();
	}

	public void setUri(Uri.Builder uri) {
		this.uri = uri;
		// uri.appendQueryParameter("isIphone", "1");
		// 在有token的时候每次请求都追加一个token;
		if (!"".equals(token)) {
			uri.appendQueryParameter("oauth_token", token);
		}
		if (!"".equals(secretToken)) {
			uri.appendQueryParameter("oauth_token_secret", secretToken);
		}
	}

	public Uri.Builder getUri() {
		return uri;
	}

	public static String getToken() {
		return Request.token;
	}

	public static String getSecretToken() {
		return Request.secretToken;
	}

	public static void setToken(String token) {
		Request.token = token;
	}

	public static void setSecretToken(String secretToken) {
		Request.secretToken = secretToken;
	}

	/**
	 * 已经创建好了http对象。对象开始进行调用和请求运行
	 * 
	 * @return JSONArray
	 * @throws ResponseTimeoutException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public Object run() throws ClientProtocolException, IOException,
			HostNotFindException {
		if ("".equals(uri))
			throw new ClientProtocolException("非法调用，执行请求时必须设置uri对象");
		HttpRequestBase http = this.executeObject();
		HttpResponse httpResp = null;
		String result = "ERROR";

		try {
			httpResp = ThinksnsHttpClient.getHttpClient().execute(http);
			int code = httpResp.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == code) {
				result = TSUIUtils.JSONFilterBom(EntityUtils.toString(
						httpResp.getEntity(), HTTP.UTF_8));
			} else if (HttpStatus.SC_NOT_FOUND == code) {
				throw new HostNotFindException("无效的请求");
			} else if (HttpStatus.SC_INTERNAL_SERVER_ERROR == code) {
				throw new HostNotFindException("服务器错误");
			}
			Log.d("Request", "Request" + code);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TSCons.APP_TAG, "reuquest run wm " + e.toString());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.d(TSCons.APP_TAG, "request  wm " + e.toString());
			throw new HostNotFindException("无效的请求");
		} catch (Exception e) {
			Log.d(TSCons.APP_TAG, "request  wm " + e.toString());
			throw new HostNotFindException("无效的请求地址");
		}
		return result;
	}

	public abstract Request append(String name, Object value);

	protected abstract HttpRequestBase executeObject();
}
