package com.thinksns.api;

import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.NotifyCount;
import com.thinksns.model.User;  


public interface ApiUsers {
	static final String MOD_NAME = "User";
	static final String SHOW     = "show";
	static final String NOTIFICATION_COUNT ="notificationCount";
	static final String UNSET_NOTIFICATION_COUNT ="unsetNotificationCount";
	
	User show(User user) throws ApiException,DataInvalidException,VerifyErrorException;
	public NotifyCount notificationCount(int uid) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException;
	boolean unsetNotificationCount(NotifyCount.Type type,int uid) throws ApiException, ListAreEmptyException,DataInvalidException,VerifyErrorException;
	
}
