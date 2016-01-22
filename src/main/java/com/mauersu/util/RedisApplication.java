package com.mauersu.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import com.mauersu.dao.RedisTemplateFactory;
import com.mauersu.exception.RedisConnectionException;
import com.mauersu.util.ztree.RedisZtreeUtil;

public abstract class RedisApplication implements Constant{

	public static volatile RefreshModeEnum refreshMode = RefreshModeEnum.manually;
	public static String BASE_PATH = "/redis-admin";
	protected RedisTemplateFactory redisTemplateFactory;
	
	protected volatile Semaphore limitUpdate = new Semaphore(1);
	protected static final int LIMIT_TIME = 3; //unit : second
	
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
			throw new RedisConnectionException("redisConnectionThreadLocal is null");
		}
		return redisConnection;
	}
	
	protected void createRedisConnection(String name, String host, int port, String password) {
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		connectionFactory.setHostName(host);
		connectionFactory.setPort(port);
		if(!StringUtils.isEmpty(password))
			connectionFactory.setPassword(password);
		connectionFactory.afterPropertiesSet();
		RedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.afterPropertiesSet();
		RedisApplication.redisTemplatesMap.put(name, redisTemplate);
		
		Map<String, Object> redisServerMap = new HashMap<String, Object>();
		redisServerMap.put("name", name);
		redisServerMap.put("host", host);
		redisServerMap.put("port", port);
		redisServerMap.put("password", password);
		RedisApplication.redisServerCache.add(redisServerMap);
		
		initRedisKeysCache(redisTemplate, name, 0);
		
		RedisZtreeUtil.initRedisNavigateZtree(name);
	}
	
	protected void initRedisKeysCache(RedisTemplate redisTemplate, String serverName , int dbIndex) {
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		connection.select(dbIndex);
		Set<byte[]> keysSet = connection.keys("*".getBytes());
		List<RKey> tempList = new ArrayList<RKey>();
		ConvertUtil.convertByteToString(connection, keysSet, tempList);
		CopyOnWriteArrayList<RKey> redisKeysList = new CopyOnWriteArrayList<RKey>(tempList);
		redisKeysListMap.put(serverName+dbIndex, redisKeysList);
		connection.select(dbIndex);
	}
}
