package com.mauersu.exception;

import java.io.IOException;

public class RedisInitException extends RuntimeException {

	public RedisInitException(Exception e) {
		super(e);
	}
	
}
