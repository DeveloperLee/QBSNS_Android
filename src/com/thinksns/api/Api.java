package com.thinksns.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.android.R;
import com.thinksns.constant.TSCons;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.CommentListAreEmptyException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.HostNotFindException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.SiteDataInvalidException;
import com.thinksns.exceptions.UpdateContentBigException;
import com.thinksns.exceptions.UpdateContentEmptyException;
import com.thinksns.exceptions.UpdateException;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.exceptions.UserListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.exceptions.WeiBoListAreEmptyException;
import com.thinksns.exceptions.WeiboDataInvalidException;
import com.thinksns.model.ApproveSite;
import com.thinksns.model.Comment;
import com.thinksns.model.Follow;
import com.thinksns.model.NotifyCount;
import com.thinksns.model.NotifyCount.Type;
import com.thinksns.model.ReceiveComment;
import com.thinksns.model.SearchUser;
import com.thinksns.model.SociaxItem;
import com.thinksns.model.User;
import com.thinksns.model.VersionInfo;
import com.thinksns.model.Weibo;
import com.thinksns.model.ListData;
import com.thinksns.net.Get;
import com.thinksns.net.Post;
import com.thinksns.net.Request;
import com.thinksns.unit.Compress;
import com.thinksns.unit.Encrypt;
import com.thinksns.unit.FormFile;
import com.thinksns.unit.FormPost;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class Api {
	public static final String TAG = "ThinksnsApi";

	public static enum Status {
		REQUESTING, SUCCESS, ERROR, RESULT_ERROR, REQUEST_ENCRYP_KEY
	}


	private static String mHost;
	private static String mPath;
	public static String mPort;
	private String url;
	private static Context mContext;
	private static Request post;
	private static Request get;
	private static Api instance;
	private static final String APP_NAME = "api";
    
	
	private Api(Context context) {
		Api.setContext(context);
		String[] configHost = context.getResources().getStringArray(
				R.array.Http);
		Api.setHost(configHost[0]);
		Api.setPath(configHost[1]);
		Api.post = new Post();
		Api.get = new Get();
	}

	private Api(String host, String path, Context context) {
		Api.setContext(context);
		Api.setHost(host);
		Api.setPath(path);
		Api.post = new Post();
		Api.get = new Get();
	}
	private Api(String host, String path, String port, Context context) {
		this(host, path, context);
		Api.mPort = port;
		Api.post = new Post();
		Api.get = new Get();
	}
    
	public static Api getInstance(Context context,boolean type,String[] url) {
		
			if(!type){
				Api.instance = new Api(context);
			}else{
				Api.instance = new Api(url[0],url[1],url[2],context);		
			}
			
		return Api.instance;
	}
    
	/**
	 * 动态配置post/get的目标Uri
	 * @param mod
	 * @param act
	 * @return
	 */
	public static Uri.Builder createUrlBuild(String mod, String act) {
		Uri.Builder uri = new Uri.Builder();
		uri.scheme("http");
		uri.authority(Api.getHost());
		uri.appendEncodedPath(Api.getPath());
		uri.appendQueryParameter("app", Api.APP_NAME);
		uri.appendQueryParameter("mod", mod);
		uri.appendQueryParameter("act", act);
		return uri;
	}
	
	private static Uri.Builder createForCheck(String api,String mod,String act){
		Uri.Builder uri = new Uri.Builder();
		uri.scheme("http");
		uri.authority(Api.getHost());
		uri.appendEncodedPath(Api.getPath());
		uri.appendQueryParameter("app", api);
		uri.appendQueryParameter("mod", mod);
		uri.appendQueryParameter("act", act);
		return uri;			
	}
	
	private static Uri.Builder createThinksnsUrlBuild(String api,String mod,String act){
		Uri.Builder uri = new Uri.Builder();
		uri.scheme("http");
		//uri.authority("10.10.67.59/QBSNS_Enterprise");   
		uri.authority("sns.neusoft.com");
		uri.appendEncodedPath("");
		//uri.appendEncodedPath(Api.getPath());
		uri.appendQueryParameter("app", api);
		uri.appendQueryParameter("mod", mod);
		uri.appendQueryParameter("act", act);
		Api.mPort = "80" ;
		return uri;		
	}
	
	/**
	 * 执行相应的requst请求
	 * @param req
	 * @return
	 * @throws ApiException
	 */
	private static Object run(Request req) throws ApiException  {
		try {
			return req.run();
		} catch (ClientProtocolException e) {
			throw new ApiException(e.getMessage());
		} catch (HostNotFindException e) {
			// TODO Auto-generated catch block
			throw new ApiException(e.getMessage());
		} catch (IOException e) {
			throw new ApiException("网络服务故障,请稍后重试");
		}
	}

	private static Status checkResult(Object result) {
		if (result.equals(Api.Status.ERROR)) {
			return Api.Status.ERROR;
		}
		return Api.Status.SUCCESS;
	}
	
    /**
     * 检验是否认证失败，如果返回的JSON对象中含有code或message
     * 则验证失败，屏幕上的dialog中打印出相应的错误信息
     * @param result
     * @throws VerifyErrorException
     * @throws ApiException
     * @throws JSONException 
     */
	private static String checkHasVerifyError(JSONObject result)
			throws VerifyErrorException, ApiException, JSONException {
		if (result.has("code") && result.has("message")) {
			try {
				throw new VerifyErrorException(result.getString("message"));
			} catch (JSONException e) {
				throw new ApiException("暂无更多数据");
			}
		}
		if(result.has("code") && result.has("message")){
		    return result.getString("message");
		}else{
		    
		}   return "";
	}

	/**
	 * 用户认证流程 采用OAuth认证方式
	 * @author lizihao
	 */
	public static final class Oauth implements ApiOauth {
		
		private String encryptKey;

		public Oauth() {
		}
        
		/**
		 * 认证流程 result为post后得到的结果用于检验是否验证成功
		 * 若验证成功 result内应该包含不为空的oauth_token和oauth_token_secret
		 * @param user_name,user_password
		 * @return 
		 * @author Lizihao
		 */
		@Override
		public User authorize(String uname, String password)
				throws ApiException, UserDataInvalidException,
				VerifyErrorException {
			String content = "";
			Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,  
					ApiOauth.AUTHORIZE);      
			Post post = new Post(); 
			post.setUri(uri);
			try {
				post.append("uid", uname)
				.append("passwd", Encrypt.AES(password,"qBT365-uRLaESkEY", "qBT365-uRLaESkEY")).
				append("isIphone", "1");
			} catch (Exception e1) {
				throw new ApiException("账号密码加密失败");
			}
			Object result = Api.run(post);
			Api.checkResult(result);
			try {

				System.out.println((String) result);
				String.valueOf(result);
				JSONObject data = new JSONObject((String) result);
				
				
				content = Api.checkHasVerifyError(data);
				String oauth_token = data.getString("oauth_token");
				String oauth_token_secret = data
						.getString("oauth_token_secret");
				int uid = data.getInt("uid");
				return new User(uid, uname, password, oauth_token,
						oauth_token_secret);
			} catch (JSONException e) {
				Log.d(TSCons.APP_TAG, e.toString());
				throw new UserDataInvalidException(content);
			}

		}

		public void setEmptyKey() {
			this.encryptKey = "";
		}
		
		/**
		 * 获取用于加密的key,系统默认为thinksns
		 */
		@Override
		public Status requestEncrypKey() throws ApiException {
			Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
					ApiOauth.REQUEST_ENCRYP);
			Get get = new Get();
			get.setUri(uri);
			Object result = Api.run(get);
			Api.checkResult(result);
			try {
				JSONArray encrypt = new JSONArray((String) result);
				this.encryptKey = encrypt.getString(0);
			} catch (JSONException e) {
				return Api.Status.RESULT_ERROR;
			}
			return Api.Status.REQUEST_ENCRYP_KEY;
		}

	}

	public static final class Statuses implements ApiStatuses {
		@Override
		public Weibo show(int id) throws ApiException,
				WeiboDataInvalidException, VerifyErrorException {
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
					ApiStatuses.SHOW);
			Get get = new Get();
			get.setUri(uri);
			get.append("id", id);
			Object result = Api.run(get);
			Api.checkResult(result);
			try {
				JSONObject data = new JSONObject((String) result);
				Api.checkHasVerifyError(data);
				return new Weibo(data);
			} catch (JSONException e) {
				Log.d(TSCons.APP_TAG, "weibo show method wm " + e.toString());
				Log.d(TSCons.APP_TAG, "weibo show method wm " + uri);
				throw new WeiboDataInvalidException("数据解析错误");
			}
		}
		
		
		@Override
		public boolean destroyWeibo(Weibo weibo) throws ApiException,
				VerifyErrorException, DataInvalidException {
			// TODO Auto-generated method stub
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
					ApiStatuses.DESTROY);
			Post post = new Post();
			post.setUri(uri);
			post.append("id", weibo.getWeiboId());
			post.append("uid", weibo.getUid());
			Object result = Api.run(post);
			Api.checkResult(result);
			if(result.equals("\"false\"")) return false;
			return true;
		}


		@Override
		public boolean destroyComment(Comment coment) throws ApiException,
				VerifyErrorException, DataInvalidException {
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
					ApiStatuses.COMMENT_DESTROY);
			Post post = new Post();
			post.setUri(uri);
			post.append("id", coment.getCommentId());
			Object result = Api.run(post);
			Api.checkResult(result);
			Integer temp = new Integer((String)result);
			if(temp.equals("1")) return false;
			return true;
		}


		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> search(String key,int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.SEARCH);
			Api.get.append("key", key);
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> searchHeader(String key,Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.SEARCH);
			Api.get.append("since_id", item.getWeiboId());
			Api.get.append("key", key);
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> searchFooter(String key,Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.SEARCH);
			Api.get.append("max_id", item.getWeiboId());
			Api.get.append("key", key);
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> mentions(int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.MENTION);
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}

		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> mentionsHeader(Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.MENTION);
			Api.get.append("since_id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}


		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> mentionsFooter(Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.MENTION);
			Api.get.append("max_id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine( count, ListData.DataType.WEIBO);
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> friendsTimeline(int count) throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.FRIENDS_TIMELINE);
			return (ListData<SociaxItem>) this.afterTimeLine( count, ListData.DataType.WEIBO);
		}
        
		
		/**
		 * 该方法在主页右上角刷新按钮被点击时被高层UI监听器调用
		 */
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> friendsHeaderTimeline(Weibo item,
				int count) throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			
			this.beforeTimeline(ApiStatuses.FRIENDS_TIMELINE);
			Api.get.append("since_id", item.getWeiboId());
			return (ListData<SociaxItem>)this.afterTimeLine( count, ListData.DataType.WEIBO);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> friendsFooterTimeline(Weibo item,
				int count) throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.FRIENDS_TIMELINE);
			Api.get.append("max_id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine( count, ListData.DataType.WEIBO);
		}
		
		
	   /**
	    * 获取用户当前全部微博信息。
	    */
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> publicTimeline(int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.PUBLIC_TIMELINE);
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> publicHeaderTimeline(Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.PUBLIC_TIMELINE);
			Api.get.append("since_id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> publicFooterTimeline(Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.PUBLIC_TIMELINE);
			Api.get.append("max_id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> userTimeline(User user, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.USER_TIMELINE);
			Api.get.append("user_id", user.getUid());
			return (ListData<SociaxItem>) this.afterTimeLine( count, ListData.DataType.WEIBO);
		}
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> userHeaderTimeline(User user, Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.USER_TIMELINE);
			Api.get.append("since_id", item.getWeiboId());
			Api.get.append("user_id", user.getUid());
			return (ListData<SociaxItem>) this.afterTimeLine( count, ListData.DataType.WEIBO);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> userFooterTimeline(User user, Weibo item, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiStatuses.USER_TIMELINE);
			Api.get.append("max_id", item.getWeiboId());
			Api.get.append("user_id", user.getUid());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.WEIBO);
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<Comment> commentTimeline(int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENT_TIMELINE);
			return (ListData<Comment>) this.afterTimeLine(count, ListData.DataType.COMMENT);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<Comment> commentHeaderTimeline(Comment item,int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENT_TIMELINE);
			Api.get.append("since_id", item.getCommentId());
			return (ListData<Comment>) this.afterTimeLine(count, ListData.DataType.COMMENT);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<Comment> commentFooterTimeline(Comment item,int count)
				throws ApiException, VerifyErrorException,ListAreEmptyException, DataInvalidException
				 {
			this.beforeTimeline(ApiStatuses.COMMENT_TIMELINE);
			Api.get.append("max_id", item.getCommentId());
			return (ListData<Comment>) this.afterTimeLine( count, ListData.DataType.COMMENT);
			// TODO Auto-generated method stub
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<Comment> commentMyTimeline(int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENT_BY_ME);
			return (ListData<Comment>) this.afterTimeLine(count, ListData.DataType.COMMENT);
		}
		@SuppressWarnings("unchecked")
		@Override
		public ListData<Comment> commentMyHeaderTimeline(Comment item,int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENT_BY_ME);
			Api.get.append("since_id", item.getCommentId());
			return (ListData<Comment>) this.afterTimeLine(count, ListData.DataType.COMMENT);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<Comment> commentMyFooterTimeline(Comment item,int count)
				throws ApiException, VerifyErrorException,ListAreEmptyException, DataInvalidException
				 {
			this.beforeTimeline(ApiStatuses.COMMENT_BY_ME);
			Api.get.append("max_id", item.getCommentId());
			return (ListData<Comment>) this.afterTimeLine( count, ListData.DataType.COMMENT);
			// TODO Auto-generated method stub
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> commentForWeiboTimeline(Weibo item,int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENTS);
			Api.get.append("id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine( count, ListData.DataType.COMMENT);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> commentForWeiboHeaderTimeline(Weibo item,Comment comment,int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENTS);
			if(comment == null){
				return commentForWeiboTimeline(item, count);
			}
			Api.get.append("since_id", comment.getCommentId());
			Api.get.append("id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine( count, ListData.DataType.COMMENT);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> commentForWeiboFooterTimeline(Weibo item,Comment comment,int count)
				throws ApiException, VerifyErrorException,ListAreEmptyException, DataInvalidException
				 {
			this.beforeTimeline(ApiStatuses.COMMENTS);
			Api.get.append("max_id",  comment.getCommentId());
			Api.get.append("id", item.getWeiboId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.COMMENT);
			// TODO Auto-generated method stub
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> commentReceiveMyTimeline(int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENT_RECEIVE_ME);
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.RECEIVE);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> commentReceiveMyHeaderTimeline(Comment item,int count)
				throws ApiException, DataInvalidException, VerifyErrorException, ListAreEmptyException {
			this.beforeTimeline(ApiStatuses.COMMENT_RECEIVE_ME);
			Api.get.append("since_id", item.getCommentId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.RECEIVE);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> commentReceiveMyFooterTimeline(Comment item,int count)
				throws ApiException, VerifyErrorException,ListAreEmptyException, DataInvalidException
				 {
			this.beforeTimeline(ApiStatuses.COMMENT_RECEIVE_ME);
			Api.get.append("max_id",  item.getCommentId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.RECEIVE);
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> following(User user,int count) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException{
			this.beforeTimeline(ApiStatuses.FOOLOWING);
			Api.get.append("user_id", user.getUid());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.FOLLOW);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> followingHeader(User user,Follow firstUser,int count) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException{
			this.beforeTimeline(ApiStatuses.FOOLOWING);
			Api.get.append("user_id", user.getUid());
			Api.get.append("since_id", firstUser.getFollowId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.FOLLOW);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> followingFooter(User user,Follow lastUser,int count) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException{
			this.beforeTimeline(ApiStatuses.FOOLOWING);
			Api.get.append("user_id", user.getUid());
			Api.get.append("max_id", lastUser.getFollowId() );
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.FOLLOW);
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> followers(User user,int count) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException{
			this.beforeTimeline(ApiStatuses.FOLLOWERS);
			Api.get.append("user_id", user.getUid());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.FOLLOW);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> followersHeader(User user,Follow firstUser,int count) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException{
			this.beforeTimeline(ApiStatuses.FOLLOWERS);
			Api.get.append("user_id", user.getUid());
			Api.get.append("since_id", firstUser.getFollowId());
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.FOLLOW);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> followersFooter(User user,Follow lastUser,int count) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException{
			this.beforeTimeline(ApiStatuses.FOLLOWERS);
			Api.get.append("user_id", user.getUid());
			Api.get.append("max_id", lastUser.getFollowId() );
			return (ListData<SociaxItem>) this.afterTimeLine(count, ListData.DataType.FOLLOW);
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> searchUser(String user, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiStatuses.SEARCH_USER);
			Api.get.append("key",user);
			
			return (ListData<SociaxItem>)afterTimeLine(count, ListData.DataType.SEARCH_USER);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> searchHeaderUser(String user, User firstUser,
				int count) throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			// TODO Auto-generated method stub
			this.beforeTimeline(ApiStatuses.SEARCH_USER);
			Api.get.append("key",user);
			Api.get.append("max_id", firstUser.getUid());
			return (ListData<SociaxItem>)afterTimeLine(count, ListData.DataType.SEARCH_USER);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ListData<SociaxItem> searchFooterUser(String user, User lastUser,
				int count) throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiStatuses.SEARCH_USER);
			Api.get.append("key",user);
			Api.get.append("since_id", lastUser.getUid());
			return (ListData<SociaxItem>)afterTimeLine(count, ListData.DataType.SEARCH_USER);
		}
		
		
		@Override
		public int update(Weibo weibo) throws ApiException,
				VerifyErrorException, UpdateException {
			if(weibo.isNullForContent()) throw new UpdateContentEmptyException();
			if(!weibo.checkContent()) throw new UpdateContentBigException();
			
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,ApiStatuses.UPDATE);
			Api.post.setUri(uri);
			Api.post.append("content", weibo.getContent());
			Object result = Api.run(Api.post);
			Api.checkResult(result);
			String data = (String)result;
			if(data.equals("false")) throw new UpdateException();
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject tempData = new JSONObject(data);
					Api.checkHasVerifyError(tempData);
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			return Integer.parseInt(data);
		}
		
		@Override
		public boolean comment(Comment comment) throws ApiException,
				VerifyErrorException, UpdateException,
				DataInvalidException {
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,ApiStatuses.COMMENT);
			comment.checkCommentCanAdd();
			
			Post post = new Post();
			post.setUri(uri);

			post.append("comment_content",comment.getContent())
					.append("weibo_id", comment.getStatus().getWeiboId() + "")
					.append("transpond",comment.getType().ordinal()+"");
			
			if(!comment.isNullForReplyComment()){
				int replyCommentId = comment.getReplyComment().getCommentId();
				post.append("reply_comment_id",  replyCommentId + "");
			}
			
			Object result = Api.run(post);
			Api.checkResult(result);
			String data = (String)result;
			if(data.equals("\"0\"")) throw new UpdateException();
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject tempData = new JSONObject(data);
					Api.checkHasVerifyError(tempData);
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			return true;
		}


		@Override
		public boolean upload(Weibo weibo,File file) throws ApiException,
				VerifyErrorException, UpdateException {
			String result = null;
			try {
				Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,ApiStatuses.UPLOAD);
				//
				FormFile formFile = new FormFile(Compress.compressPic(file),file.getName(),"pic","application/octet-stream") ;
				Api.post.setUri(uri);
				HashMap<String,String> param = new HashMap<String,String>();
				param.put("content", weibo.getContent());
				param.put("token", Request.getToken());
				param.put("secretToken", Request.getSecretToken());
				param.put("from", Weibo.From.ANDROID.ordinal()+"");
				result = FormPost.post(uri.toString(),param, formFile);
			} catch (FileNotFoundException e) {
				throw new UpdateException("file not found!");
			} catch (IOException e) {
				throw new UpdateException("file upload faild");
			}
			try {
				Api.checkHasVerifyError(new JSONObject(result));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Integer.parseInt(result) > 0;
		}
		

		@Override
		public boolean repost(Weibo weibo,boolean comment) throws ApiException,
				VerifyErrorException, UpdateException, DataInvalidException {
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,ApiStatuses.REPOST);
			
			Api.post.setUri(uri);
			Api.post.append("content", weibo.getContent());
			Api.post.append("transpond_id", weibo.getTranspond().getWeiboId()+"");
			if(comment){
				Api.post.append("reply_data", weibo.getTranspond().getWeiboId() + "");
			}
			Object result = Api.run(Api.post);
			Api.checkResult(result);
			String data = (String)result;
			if(data.equals("\"0\"") || data.equals("\"false\"")) throw new UpdateException();
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject tempData = new JSONObject(data);
					Api.checkHasVerifyError(tempData);
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			return true;
		}
		/**
		 * 在发送request以前配置get的目标Uri
		 * @param act
		 */
		private void beforeTimeline(String act){
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
					act);
			Log.e("uri","uri+"+uri.toString());
			Api.get.setUri(uri);
		}
		
		/**
		 * 当再次请求获取微博列表时执行的最底层服务器请求。
		 * @param count
		 * @param type
		 * @return
		 * @throws ApiException
		 * @throws ListAreEmptyException
		 * @throws VerifyErrorException
		 * @throws DataInvalidException
		 */
		private ListData<?> afterTimeLine( int count,ListData.DataType type) throws ApiException, ListAreEmptyException,
		VerifyErrorException, DataInvalidException{
			Api.get.append("count", count);
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			if(type == ListData.DataType.COMMENT||type==ListData.DataType.RECEIVE){
				if (result.equals("null")) throw new CommentListAreEmptyException();
				}else if(type == ListData.DataType.WEIBO){
				if (result.equals("null")) throw new WeiBoListAreEmptyException();
			}else if(type == ListData.DataType.USER || type == ListData.DataType.FOLLOW || type == ListData.DataType.SEARCH_USER){
				if (result.equals("null")) throw new UserListAreEmptyException();
			}
			try {
				JSONArray data = new JSONArray((String) result);
				int length = data.length();
				ListData<SociaxItem> list = new ListData<SociaxItem>();   
				if (length ==0 ) throw new ListAreEmptyException();  
			
				for(int i=0;i<length;i++){
					JSONObject itemData = data.getJSONObject(i);
					try {
						SociaxItem weiboData = this.getSociaxItem(type, itemData);//当前type为Weibo，返回的是weibo类型对象并添加入list
						if(!weiboData.checkValid()) continue;  //检验当前的weibodata是否为有效的
						list.add(weiboData);
					} catch (DataInvalidException e) {
						Log.e(TAG, "has one invalid item with string:"+e.toString());
						Log.e(TAG, "has one invalid item with string:"+data.getString(i));
						continue;
					}
				}
				if(list.size() == 0) throw new ListAreEmptyException();
				return list;
			} catch (JSONException e) {   //检查返回值，如果是一个JSONObject,则进行一次验证看看是否是验证失败得提信息
				try {
					JSONObject data = new JSONObject((String) result);
					Api.checkHasVerifyError(data);
					throw new CommentListAreEmptyException();
				} catch (JSONException e1) {
					throw new ApiException("无效的数据格式");
				}
			}
		}
		
		private SociaxItem getSociaxItem(ListData.DataType type,JSONObject jsonObject) throws DataInvalidException, ApiException{
			if(type == ListData.DataType.COMMENT){
				return new Comment(jsonObject);
			}else if(type == ListData.DataType.WEIBO){
				return new Weibo(jsonObject);
			}else if(type == ListData.DataType.USER){
				return new User(jsonObject);
			}else if(type == ListData.DataType.RECEIVE){
				return new ReceiveComment(jsonObject);
			}else if(type == ListData.DataType.FOLLOW){
				return new Follow(jsonObject);
			}else if(type == ListData.DataType.SEARCH_USER){
				return new SearchUser(jsonObject);
			}else{
				throw new ApiException("参数错误");
			}
		}


		@Override
		public int unRead() throws ApiException,
				VerifyErrorException, DataInvalidException {
			// TODO Auto-generated method stub
			Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,ApiStatuses.UN_READ);
			Api.get.setUri(uri);
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			String data = (String)result;
		//	if(data.equals("false")) throw new UpdateException();
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject tempData = new JSONObject(data);
					Api.checkHasVerifyError(tempData);
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			return Integer.parseInt(data);
		}

	} 
	
	public static final class Message implements ApiMessage{

		@Override
		public ListData<SociaxItem> inbox(int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.BOX);
			Api.get.append("count", count);
			ListData<SociaxItem> list= new ListData<SociaxItem>();
			this.getMessageList(list,true);
			return list;
		}

		@Override
		public ListData<SociaxItem> inboxHeader(
				com.thinksns.model.Message message, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.BOX);
			Api.get.append("count", count);
			Api.get.append("since_id", message.getListId());
			ListData<SociaxItem> list= new ListData<SociaxItem>();
			this.getMessageList(list,true);
			return list;
		}

		@Override
		public ListData<SociaxItem> inboxFooter(
				com.thinksns.model.Message message, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.BOX);
			Api.get.append("count", count);
			Api.get.append("max_id", message.getListId());
			ListData<SociaxItem> list= new ListData<SociaxItem>();
			this.getMessageList(list,true);
			return list;
		}

		@Override
		public ListData<SociaxItem> outbox(com.thinksns.model.Message message,int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.SHOW);
			Api.get.append("id", message.getListId());
			Api.get.append("count", count);
			ListData<SociaxItem> list= new ListData<SociaxItem>();
			this.getMessageList(list,false);
			return list;
		}

		@Override
		public ListData<SociaxItem> outboxHeader(
				com.thinksns.model.Message message, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.SHOW);
			Api.get.append("count", count);
			Api.get.append("id", message.getListId());
			Api.get.append("since_id", message.getMeesageId());
			ListData<SociaxItem> list= new ListData<SociaxItem>();
			this.getMessageList(list,false);
			return list;
		}

		@Override
		public ListData<SociaxItem> outboxFooter(
				com.thinksns.model.Message message, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.SHOW);
			Api.get.append("count", count);
			Api.get.append("id", message.getListId());
			Api.get.append("max_id", message.getMeesageId());
			ListData<SociaxItem> list= new ListData<SociaxItem>();
			this.getMessageList(list,false);
			return list;
		}

		@Override
		public com.thinksns.model.Message show(
				com.thinksns.model.Message message)
				throws ApiException, DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.SHOW);
			Api.get.append("id", message.getListId());
			Api.get.append("show_cascade", 0);
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			try {
				JSONObject data = new JSONObject((String)result);
				Api.checkHasVerifyError(data);
				return new com.thinksns.model.Message(data);
			} catch (JSONException e) {
				throw new ApiException();
			}
		}
		
		//获取私信列表
		private void getMessageList(ListData<SociaxItem> list,boolean type) throws DataInvalidException, VerifyErrorException, ApiException{
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			try {
				JSONArray data = new JSONArray((String)result);
				int length = data.length();
				com.thinksns.model.Message mainMessage = null;
				for(int i = 0; i < length;i++){
				 if(type){
					  mainMessage = new com.thinksns.model.Message(data.getJSONObject(i));
				 }else{
					 mainMessage = new com.thinksns.model.Message(data.getJSONObject(i),false); 
				 }
				//	if(!tempData.checkValid()) continue;
					list.add(mainMessage);
				}
			} catch (JSONException e) {
				JSONObject data;
				try {
					data = new JSONObject((String)result);
					Api.checkHasVerifyError(data);
					throw new ApiException();
				} catch (JSONException e1) {
					throw new ApiException();
				}
			}
		}
		
		@Override
		public boolean createNew(com.thinksns.model.Message message) throws ApiException,
				DataInvalidException, VerifyErrorException {
		//	message.checkMessageCanAdd();
			Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,ApiMessage.CREATE);
			Api.post.setUri(uri);
			Api.post.append("to_uid", message.getToUid());
			Api.post.append("title", message.getTitle()).append("content", message.getContent());
			Object result = Api.run(Api.post);
			Api.checkResult(result);
			if(result.equals("\"false\"")||result.equals("\"0\"")) return false;
			return true;
		}

		@Override
		public void show(
				com.thinksns.model.Message message,ListData<SociaxItem> list)
				throws ApiException, DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiMessage.SHOW);
			Api.get.append("id", message.getListId());
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			try {
				JSONArray data = new JSONArray((String)result);
				int length = data.length();
				com.thinksns.model.Message mainMessage = null;
				for(int i = 0; i < length;i++){
					com.thinksns.model.Message tempData = new com.thinksns.model.Message(data.getJSONObject(i));
					if(i==0){
						mainMessage = tempData;
					}
					
					if(!tempData.checkValid()) continue;
					list.add(tempData);
				}
			} catch (JSONException e) {
				JSONObject data;
				try {
					data = new JSONObject((String)result);
					Api.checkHasVerifyError(data);
					throw new ApiException();
				} catch (JSONException e1) {
					throw new ApiException();
				}
			}
		}

		@Override
		public int[] create(com.thinksns.model.Message message)
				throws ApiException, DataInvalidException, VerifyErrorException {
			//message.checkMessageCanAdd();
			Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,ApiMessage.CREATE);
			Api.post.setUri(uri);
			Api.post.append("title", message.getTitle()).append("content", message.getContent());
			Object result = Api.run(Api.post);
			Api.checkResult(result);
			try {
				JSONArray data = new JSONArray((String)result);
				int[] res = new int[data.length()];
				for(int i =0;i<data.length();i++){
					res[i] = data.getInt(i);
				}
				return res;
			} catch (JSONException e) {
				try{
					JSONObject data = new JSONObject((String)result);
					Api.checkHasVerifyError(data);
					throw new ApiException();
				}catch(JSONException e2){
					throw new ApiException();
				}
			
			}
						
		}
		
		/**
		 * 回复私信
		 */
		@Override
		public boolean reply(com.thinksns.model.Message message)
				throws ApiException, DataInvalidException, VerifyErrorException {
		//	message.checkMessageCanReply();
			Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,ApiMessage.REPLY);
			Api.post.setUri(uri);
		//	Api.post.append("id", message.getSourceMessage().getMessageId()).append("content", message.getContent());
			Api.post.append("id", message.getListId());
			Api.post.append("content", message.getContent());
			Object result = Api.run(Api.post);
			Api.checkResult(result);
			if(result.equals("\"false\"")||result.equals("\"0\"")) return false;
			return true;
		}
		
		private void beforeTimeline(String act){
			Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
					act);
			Api.get.setUri(uri);
			
			
		}
	}
	
	public static final class Friendships implements ApiFriendships{
		@Override
		public boolean show(User friends) throws ApiException,
				VerifyErrorException {
			this.beforeTimeline(ApiFriendships.SHOW);
			Api.get.append("user_id",friends.getUid());
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			try {
				JSONObject data = new JSONObject((String)result);
				Api.checkHasVerifyError(data);
			} catch (JSONException e) {
				throw new ApiException();
			}
			String resultString = (String)result;
			return resultString.equals("\"havefollow\"")  || resultString.equals("\"eachfollow\"");
		}
		private void beforeTimeline(String act){
			Uri.Builder uri = Api.createUrlBuild(ApiFriendships.MOD_NAME,
					act);
			Api.get.setUri(uri);
		}
		
		
		@Override
		public boolean create(User user) throws ApiException,
				VerifyErrorException,DataInvalidException {
			return this.doApiRuning(user, Api.post, ApiFavorites.CREATE);
		}
		@Override
		public boolean destroy(User user) throws ApiException,
				VerifyErrorException,DataInvalidException {
			// TODO Auto-generated method stub
			return this.doApiRuning(user, Api.post, ApiFavorites.DESTROY);
		}
		@Override
		public boolean addBlackList(User user) throws ApiException,VerifyErrorException,DataInvalidException{
			return this.doApiRuning(user, Api.post, ApiFriendships.ADDTOBLACKLIST);
		}
		@Override
		public boolean delBlackList(User user) throws ApiException,VerifyErrorException,DataInvalidException{
			return this.doApiRuning(user, Api.post, ApiFriendships.DELTOBLACKLIST);
		}
		private boolean doApiRuning(User user,Request res,String act) throws ApiException, VerifyErrorException, DataInvalidException{
			Uri.Builder uri = Api.createUrlBuild(ApiFriendships.MOD_NAME,act);
			if(user.isNullForUid()) throw new DataInvalidException();
			Api.post.setUri(uri);
			Api.post.append("user_id", user.getUid());
			Object result = Api.run(Api.post);
			Api.checkResult(result);
			
			String data = (String)result;
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject datas = new JSONObject((String)result);
					Api.checkHasVerifyError(datas);
					if(datas.has("is_followed")){
						String tempString = datas.getString("is_followed");
						return tempString == "havefollow"||tempString == "eachfollow"?true:false;
					}
					
					throw new ApiException();
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			if(data.equals("\"true\"")||data.equals("\"1\"")){
				return true;
			}else{
				return false;
			}
		}

		@Override
		public boolean isFollowTopic(User user, String topic)
				throws ApiException, VerifyErrorException, DataInvalidException {
			// TODO Auto-generated method stub
			return doApiRuning(ApiFriendships.ISFOLLOWTOPIC, topic);
		}
		@Override
		public boolean followTopic(User user, String topic) throws ApiException, VerifyErrorException,
				DataInvalidException {
			// TODO Auto-generated method stub
			return doApiRuning( ApiFriendships.FOLLOWTOPIC, topic);
		}
		@Override
		public boolean unFollowTopic(User user, String topic) throws ApiException,
				VerifyErrorException, DataInvalidException {
			// TODO Auto-generated method stub
			return doApiRuning(ApiFriendships.UNFOLLOWTOPIC, topic);
		}
		
		private boolean doApiRuning(String act, String topic)
				throws ApiException, VerifyErrorException, DataInvalidException {
			Uri.Builder uri = Api.createUrlBuild(ApiFriendships.MOD_NAME, act);
			Api.get.setUri(uri);
			Api.get.append("topic", topic);
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			String data = (String) result;
			System.out.println("result" + data);
			if (data.equals("ERROR")) {
				throw new ApiException("网络繁忙，请重试！");
			}
			if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
				try {
					JSONObject datas = new JSONObject((String) result);
					Api.checkHasVerifyError(datas);
					if (datas.has("is_followed")) {
						String tempString = datas.getString("is_followed");
						if (act.equals(ApiFriendships.FOLLOWTOPIC)) {
							return tempString.equals("havefollow") ? true
									: false;
						} else if (act.equals(ApiFriendships.UNFOLLOWTOPIC)) {
							return tempString.equals("unfollow") ? true : false;
						}
					}
					throw new ApiException();
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			if (data.equals("\"true\"") || data.equals("1")) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static final class Favorites implements ApiFavorites{

		@Override
		public ListData<Weibo> index(int count) throws ApiException,
				ListAreEmptyException, DataInvalidException,
				VerifyErrorException {
			this.beforeTimeline(ApiFavorites.INDEX);
			Api.get.append("count", count);
			return this.getList();
		}

		@Override
		public ListData<Weibo> indexHeader(Weibo weibo, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiFavorites.INDEX);
			Api.get.append("count", count);
			Api.get.append("since_id", weibo.getWeiboId());
			return this.getList();
		}

		@Override
		public ListData<Weibo> indexFooter(Weibo weibo, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiFavorites.INDEX);
			Api.get.append("count", count);
			Api.get.append("max_id", weibo.getWeiboId());
			return this.getList();
		}

		@Override
		public boolean create(Weibo weibo) throws ApiException,
				DataInvalidException, VerifyErrorException {
			return this.doApiRuning(weibo, Api.post, ApiFavorites.CREATE);
		}

		@Override
		public boolean isFavorite(Weibo weibo) throws ApiException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline(ApiFavorites.IS_FAVORITE);
			Api.get.append("id", weibo.getWeiboId());
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			String data = (String)result;
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject datas = new JSONObject((String)result);
					Api.checkHasVerifyError(datas);
					throw new ApiException();
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			
			return data.equals("true");
		}
		
		private void beforeTimeline(String act){
			Uri.Builder uri = Api.createUrlBuild(ApiFavorites.MOD_NAME,
					act);
			Api.get.setUri(uri);
		}

		@Override
		public boolean destroy(Weibo weibo) throws ApiException,
				DataInvalidException, VerifyErrorException {
			return this.doApiRuning(weibo, Api.post, ApiFavorites.DESTROY);
		}
		
		private boolean doApiRuning(Weibo weibo,Request res,String act) throws ApiException, VerifyErrorException, DataInvalidException{
			Uri.Builder uri = Api.createUrlBuild(ApiFavorites.MOD_NAME,act);
			if(weibo.isNullForWeiboId()) throw new DataInvalidException();
			Api.post.setUri(uri);
			Api.post.append("id", weibo.getWeiboId());
			Object result = Api.run(Api.post);
			Api.checkResult(result);
			String data = (String)result;
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject datas = new JSONObject((String)result);
					Api.checkHasVerifyError(datas);
					throw new ApiException();
				} catch (JSONException e) {
					throw new ApiException();
				}
			}
			return Integer.parseInt(data) > 0;
		}
		
		public ListData<Weibo> getList() throws VerifyErrorException, ApiException, ListAreEmptyException{
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			try {
				JSONArray data = new JSONArray((String) result);
				int length = data.length();
				ListData<Weibo> list = new ListData<Weibo>();
			
				for(int i=0;i<length;i++){
					JSONObject itemData = data.getJSONObject(i);
					try {
						Weibo weiboData = new Weibo(itemData);
						if(!weiboData.checkValid()) continue; 
						list.add(weiboData);
					} catch (WeiboDataInvalidException e) {
						Log.e(TAG, "has one invalid weibo item with string:"+data.getString(i));
					}
				}
				return list;
			} catch (JSONException e) {   //检查返回值，如果是一个JSONObject,则进行一次验证看看是否是验证失败得提示信息
				try {
					JSONObject data = new JSONObject((String) result);
					Api.checkHasVerifyError(data);
					throw new ListAreEmptyException();
				} catch (JSONException e1) {
					throw new ApiException("暂无更多数据");
				}
			}
		}
	}
	
	//多站点网站
	public static final class Sites implements ApiSites{
		@Override
		public ListData<SociaxItem> getSisteList() throws ApiException,
				ListAreEmptyException, DataInvalidException,
				VerifyErrorException {
			// TODO Auto-generated method stub
			ListData<SociaxItem> list=null;
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			try {
				if (!result.equals("null")) {
					JSONArray data = new JSONArray((String) result);
					int length = data.length();
					list = new ListData<SociaxItem>();
					for (int i = 0; i < length; i++) {
						JSONObject itemData = data.getJSONObject(i);
						try {
							SociaxItem siteData = new ApproveSite(itemData);
							list.add(siteData);
						} catch (SiteDataInvalidException e) {
							Log.e(TAG,
									"has one invalid weibo item with string:"
											+ data.getString(i));
						}
					}
				}
				return list;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				try {
					JSONObject data = new JSONObject((String) result);
					Api.checkHasVerifyError(data);
					throw new ListAreEmptyException();
				} catch (JSONException e1) {
					throw new ApiException("暂无更多数据");
				}
			}

		}

		@Override
		public boolean getSiteStatus(ApproveSite as) throws ApiException,
				ListAreEmptyException, DataInvalidException,
				VerifyErrorException {
			// TODO Auto-generated method stub
			this.beforeTimeline(ApiSites.GET_SITE_STATUS);
			Api.get.append("id", as.getSite_id());
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			try {
				JSONObject object = new JSONObject((String)result);
				 if(object.has("status")&&object.has("alias")){
					 if(object.getInt("status")==1){
						 return true;
					 }
				 }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		private void beforeTimeline(String act){
			Uri.Builder uri = Api.createThinksnsUrlBuild(Api.APP_NAME,ApiSites.MOD_NAME,
					act);
			Api.get.setUri(uri);
		}

		@Override
		public ListData<SociaxItem> newSisteList(int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			// TODO Auto-generated method stub
			this.beforeTimeline(ApiSites.GET_SITE_LIST);
			Api.get.append("count", count);
			return this.getSisteList();
		}

		@Override
		public ListData<SociaxItem> getSisteListHeader(ApproveSite as, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			// TODO Auto-generated method stub
			this.beforeTimeline(ApiSites.GET_SITE_LIST);
			Api.get.append("count", count);
			Api.get.append("since_id", as.getSite_id());
			return this.getSisteList();
		}

		@Override
		public ListData<SociaxItem> getSisteListFooter(ApproveSite as, int count)
				throws ApiException, ListAreEmptyException,
				DataInvalidException, VerifyErrorException {
			// TODO Auto-generated method stub
			this.beforeTimeline(ApiSites.GET_SITE_LIST);
			Api.get.append("count", count);
			Api.get.append("max_id",as.getSite_id());
			return this.getSisteList();
		}
		
		//dev.thinksns.com/ts/2.0/index.php?app=home&mod=Widget&act=addonsRequest&addon=Login&hook=isSinaLoginAvailable
		@Override
		public boolean isSupport() throws ApiException,
				ListAreEmptyException, DataInvalidException,
				VerifyErrorException {
			Uri.Builder uri = Api.createForCheck("home","Widget","addonsRequest");
			System.out.println("url test " + uri);
			Api.get.setUri(uri);
			Api.get.append("addon", "Login").append("hook", "isSinaLoginAvailable");
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			
			Integer object = null;
			try{
				object= new Integer((String)result);
				return object.equals(1)?true:false;
			}catch(Exception ex){
				// TODO Auto-generated catch block
				return false;
			}
		}

		
		@Override
		public boolean isSupportReg() throws ApiException,
				ListAreEmptyException, DataInvalidException,
				VerifyErrorException {
			Uri.Builder uri = Api.createForCheck("home","Public","isRegisterAvailable");
			Api.get.setUri(uri);
			
			Api.get.append("wap_to_normal", 1);
			
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			
			Integer object = null;
			try{
				object= new Integer((String)result);
				return object.equals(1)?true:false;
			}catch(Exception ex){
				// TODO Auto-generated catch block
				return false;
			}		// TODO Auto-generated method stub
		}

		@Override
		public ListData<SociaxItem> searchSisteList(String key, int count)
				throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException {
			this.beforeTimeline(ApiSites.GET_SITE_LIST);
			Api.get.append("count", count);
			Api.get.append("content", key);
			return this.getSisteList();
		}
		
		
	}
	
	//Users
	public static final class Users implements ApiUsers{

		@Override
		public User show(User user) throws ApiException,
				DataInvalidException, VerifyErrorException {
			this.beforeTimeline("show");
			Api.get.append("user_id", user.getUid());
			if(user.getUserName()!= null){
				Api.get.append("user_name", user.getUserName());
			}
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			String data = (String)result;
			if(data.equals("\"false\"")) throw new DataInvalidException("该用户不存在");
			
			try {
				JSONObject userData = new JSONObject(data);
				Api.checkHasVerifyError(userData);
				return new User(userData);
			} catch (JSONException e) {
				throw new DataInvalidException("数据格式错误");
			}
		}
		//返回通知，@，私信
		@Override
		public NotifyCount notificationCount(int uid) throws ApiException,
				ListAreEmptyException, DataInvalidException,
				VerifyErrorException {
			this.beforeTimeline(ApiUsers.NOTIFICATION_COUNT);
			Api.get.append("user_id", uid);
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			String data = (String)result;
			if(data.equals("\"false\"")) throw new ListAreEmptyException("请求的数据异常");
			
			try {
				JSONObject userData = new JSONObject(data);
				Api.checkHasVerifyError(userData);
				NotifyCount notifyCount = new NotifyCount(userData);
				return notifyCount;
			} catch (JSONException e) {
				throw new DataInvalidException("数据格式错误");
			}
		}

		@Override
		public boolean unsetNotificationCount(Type type,int uid) throws ApiException,
				ListAreEmptyException, DataInvalidException,
				VerifyErrorException {
			this.beforeTimeline(ApiUsers.UNSET_NOTIFICATION_COUNT);
			NotifyCount notifycount = new NotifyCount();
			for(NotifyCount.Type t:NotifyCount.Type.values()){
				
				if(t.equals(type)){
					String name = t.name();
					Api.get.append("type", name);
				}
			}
			Api.get.append("user_id", uid);
			Object result = Api.run(Api.get);
			Api.checkResult(result);
			String data = (String)result;
			if(data.equals("\"false\"")) return false;
			if(data.indexOf("{") != -1 || data.indexOf("[") != -1){
				try {
					JSONObject userData = new JSONObject(data);
					Api.checkHasVerifyError(userData);
					return false;
				} catch (JSONException e) {
					throw new DataInvalidException("数据格式错误");
				}
			}else{
				if(NotifyCount.Type.atMe.name() == type.name()){
					notifycount.setAtme(0);
				}else if(NotifyCount.Type.message.name() == type.name()){
					notifycount.setMessage(0);
				}else if(NotifyCount.Type.notify.name() == type.name()){
					notifycount.setNotify(0);
				}else if(NotifyCount.Type.weibo_comment.name() == type.name()){
					notifycount.setWeiboComment(0);
				}
				System.out.println("unsetNotificationCount"+type.name());
				return true;
			}
		
		}
		
		private void beforeTimeline(String act){
			Uri.Builder uri = Api.createUrlBuild("User",
					act);
			Api.get.setUri(uri);
		}

	}
	
	/**
	 * 检测软件版本
	 * @author  lizihao
	 *
	 */
	public static final class Upgrade implements ApiUpgrade{

		private Uri.Builder beforeTimeline(String act){
			return  Api.createUrlBuild(ApiUpgrade.MOD_NAME,act);
		}
		
		@Override
		public VersionInfo getVersion() throws ApiException {
			Api.get.setUri(beforeTimeline(ApiUpgrade.GET_VERSION));
			Object result = Api.run(Api.get);
			VersionInfo vInfo  = null ;
			try {
				vInfo = new VersionInfo( new JSONObject((String)result));
			} catch (JSONException e) {
				e.printStackTrace();
				throw new ApiException("数据解析错误");
			}
			return vInfo;
		}
		
	}
	
	public static String getHost() {
		return mHost;
	}

	public static String getPath() {
		return mPath;
	}

	public static Context getContext() {
		return mContext;
	}

	public static void setContext(Context context) {
		Api.mContext = context;
	}

	private static void setHost(String host) {
		Api.mHost = host;
	}

	private static void setPath(String path) {
		Api.mPath = path;
	}

	public  String getUrl() {
		return url;
	}

	public  void setUrl(String _url) {
		url = _url;
	}
	
	
}
