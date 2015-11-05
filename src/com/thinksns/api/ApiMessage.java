package com.thinksns.api;

import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.ListData; 
import com.thinksns.model.Message;
import com.thinksns.model.SociaxItem;

public interface ApiMessage {
	public static final String MOD_NAME = "Message";
	public static final String INBOX = "inbox";
	public static final String OUTBOX = "outbox";
	public static final String BOX ="box";
	public static final String SHOW   = "show";
	public static final String CREATE = "create";
	public static final String REPLY  = "reply";
	public static final String DESTROY = "destroy";
	
	ListData<SociaxItem> inbox(int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	ListData<SociaxItem> inboxHeader(Message message,int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	ListData<SociaxItem> inboxFooter(Message message,int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	
	ListData<SociaxItem> outbox(Message message,int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	ListData<SociaxItem> outboxHeader(Message message,int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	ListData<SociaxItem> outboxFooter(Message message,int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	
	Message show(Message message) throws ApiException, DataInvalidException,  VerifyErrorException;
	void show(Message message,ListData<SociaxItem> list) throws ApiException, DataInvalidException,  VerifyErrorException;
	
	int[] create(Message message) throws ApiException, DataInvalidException,  VerifyErrorException;
	
	boolean reply(Message message) throws ApiException, DataInvalidException,  VerifyErrorException;
	
	boolean createNew(Message message) throws ApiException, DataInvalidException,  VerifyErrorException;
}
