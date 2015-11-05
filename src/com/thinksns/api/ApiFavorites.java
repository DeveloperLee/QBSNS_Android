package com.thinksns.api;

import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.ListData;
import com.thinksns.model.Weibo; 

public interface ApiFavorites {
	public static final String MOD_NAME = "Favorites";
	public static final String INDEX = "index";
	public static final String CREATE = "create";
	public static final String IS_FAVORITE = "isFavorite";
	public static final String DESTROY = "destroy";

	ListData<Weibo> index(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	ListData<Weibo> indexHeader(Weibo weibo, int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	ListData<Weibo> indexFooter(Weibo weibo, int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	boolean create(Weibo weibo) throws ApiException, DataInvalidException,
			VerifyErrorException;

	boolean isFavorite(Weibo weibo) throws ApiException, DataInvalidException,
			VerifyErrorException;

	boolean destroy(Weibo weibo) throws ApiException, DataInvalidException,
			VerifyErrorException;
}
