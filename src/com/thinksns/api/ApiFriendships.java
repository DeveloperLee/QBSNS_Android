package com.thinksns.api;

import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.User;

public interface ApiFriendships {
	static final String SHOW     = "show";
	static final String MOD_NAME = "Friendships";
	static final String CREATE   = "create";
	static final String DESTROY  = "destroy";
	
	public static final String ADDTOBLACKLIST = "addToBlackList";
	public static final String DELTOBLACKLIST = "removeFromBlackList";
	/**关注话题*/
	public static final String ISFOLLOWTOPIC = "isFollowTopic";   
	/**关注话题*/
	public static final String FOLLOWTOPIC = "followTopic";
	/**取消关注话题*/
	public static final String UNFOLLOWTOPIC = "unfollowTopic";
	
	
	public boolean show(User friends) throws ApiException,VerifyErrorException;
	public boolean create(User user) throws ApiException,VerifyErrorException,DataInvalidException;
	public boolean destroy(User user) throws ApiException,VerifyErrorException,DataInvalidException;
	public boolean addBlackList(User user) throws ApiException,VerifyErrorException,DataInvalidException;
	public boolean delBlackList(User user) throws ApiException,VerifyErrorException,DataInvalidException;
	/**是否关注话题*/
	boolean isFollowTopic(User user, String topic) throws ApiException,VerifyErrorException, DataInvalidException;
	/**关注话题*/
	public boolean followTopic(User user, String topic) throws ApiException,VerifyErrorException,DataInvalidException;
	/**取消关注话题*/
	public boolean unFollowTopic(User user, String topic) throws ApiException,VerifyErrorException,DataInvalidException;
	
}
