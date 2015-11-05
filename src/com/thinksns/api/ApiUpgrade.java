package com.thinksns.api;

import com.thinksns.exceptions.ApiException;
import com.thinksns.model.VersionInfo;

public interface ApiUpgrade{
	public static final String MOD_NAME = "Upgrade";
	public static final String GET_VERSION = "getVersion";

	/**
	 * 获取版本信息
	 * @return
	 * @throws ApiException
	 */
	public VersionInfo getVersion() throws ApiException;
}
