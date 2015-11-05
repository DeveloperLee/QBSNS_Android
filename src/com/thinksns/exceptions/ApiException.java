package com.thinksns.exceptions;

public class ApiException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiException() {
		super("暂时没有更多数据");
		// TODO Auto-generated constructor stub
	}

	public ApiException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
