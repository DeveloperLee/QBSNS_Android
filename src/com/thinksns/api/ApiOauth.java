package com.thinksns.api;

import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.UserDataInvalidException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.User;

public interface ApiOauth{
	public static final String MOD_NAME = "Oauth";
	public static final String REQUEST_ENCRYP = "request_key";
	public static final String AUTHORIZE = "authorize";

	/**
	 * 认证Api
	 * @param uname      用户名
	 * @param password   密码
	 * @return
	 * @throws Exception 
	 */
	public User authorize(String uname,String password) throws ApiException,UserDataInvalidException,VerifyErrorException;
	public Api.Status requestEncrypKey() throws ApiException;
}
