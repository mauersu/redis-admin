package com.mauersu.util;

import java.util.concurrent.Semaphore;

import org.springframework.data.redis.connection.RedisConnection;

import com.mauersu.dao.RedisTemplateFactory;

public abstract class RedisApplication implements Constant{

	public boolean useVMCache = true;
	public static String BASE_PATH = "/redis-admin";
	protected RedisTemplateFactory redisTemplateFactory;
	
	protected volatile Semaphore limitUpdate = new Semaphore(1);
	protected static final int LIMIT_TIME = 1; //unit : second
	
	protected static ThreadLocal<RedisConnection> 	redisConnectionThreadLocal = new ThreadLocal<RedisConnection>() {
		@Override
		protected RedisConnection initialValue() {
			return null;
		};
	};
	protected static ThreadLocal<Semaphore> updatePermition = new ThreadLocal<Semaphore>() {
		@Override
		protected Semaphore initialValue() {
			return null;
		};
	};
	
	protected boolean getUpdatePermition() {
		updatePermition.set(limitUpdate);
		boolean permit = updatePermition.get().tryAcquire(1);
		return permit;
	}
	
	protected void finishUpdate() {
		try {
			Thread.sleep(LIMIT_TIME * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updatePermition.get().release(1);
	}
	
	protected void runUpdateLimit() {
		new Thread(new Runnable () {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(LIMIT_TIME * 1000);
						limitUpdate = new Semaphore(1);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	protected RedisConnection getRedisConnection(String redisName, int dbIndex) {
		RedisConnection redisConnection = redisConnectionThreadLocal.get();
		if(redisConnection==null) {
			redisConnection = redisTemplateFactory.getRedisConnection(redisName, dbIndex);
			redisConnectionThreadLocal.set(redisConnection);
		}
		return redisConnection;
	}
	
	protected RedisConnection getRedisConnection() {
		RedisConnection redisConnection = redisConnectionThreadLocal.get();
		if(redisConnection==null) {
			return getRedisConnection(DEFAULT_REDISSERVERNAME, DEFAULT_DBINDEX);
		}
		return redisConnection;
	}
}
