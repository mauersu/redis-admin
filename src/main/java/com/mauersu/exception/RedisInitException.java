package com.mauersu.exception;


public class RedisInitException extends RuntimeException {

	public RedisInitException(Exception e) {
		super(e);
	}
	
}
