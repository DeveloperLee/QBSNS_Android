package com.thinksns.exceptions;


public class CommentListAreEmptyException extends ListAreEmptyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommentListAreEmptyException() {
		// TODO Auto-generated constructor stub
		super("暂无更多评论");
	}

	public CommentListAreEmptyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CommentListAreEmptyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
