package com.thinksns.api;

import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.ApproveSite;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem;  

public interface ApiSites {
	public static final String MOD_NAME = "Sitelist";
	public static final String GET_SITE_LIST = "getSiteList";
	public static final String  GET_SITE_STATUS= "getSiteStatus";
	
	public  ListData<SociaxItem> getSisteList() throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	public  ListData<SociaxItem> newSisteList(int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	public  ListData<SociaxItem> getSisteListHeader(ApproveSite as, int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	public  ListData<SociaxItem> getSisteListFooter(ApproveSite as, int count) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	
	public boolean  getSiteStatus(ApproveSite as) throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	public boolean isSupport() throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;
	public boolean isSupportReg() throws ApiException, ListAreEmptyException, DataInvalidException,  VerifyErrorException;


	ListData<SociaxItem> searchSisteList(String key, int count)
			throws ApiException, VerifyErrorException, ListAreEmptyException, DataInvalidException ;
}
