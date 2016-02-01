package com.mauersu.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.mauersu.dao.RedisTemplateFactory;
import com.mauersu.exception.ConcurrentException;
import com.mauersu.util.redis.MyStringRedisTemplate;
import com.mauersu.util.ztree.RedisZtreeUtil;

public abstract class RedisApplication implements Constant{

	public static volatile RefreshModeEnum refreshMode = RefreshModeEnum.manually;
	public static volatile ShowTypeEnum showType = ShowTypeEnum.show;
	public static String BASE_PATH = "/redis-admin";
	
	protected volatile Semaphore limitUpdate = new Semaphore(1);
	protected static final int LIMIT_TIME = 3; //unit : second
	
	public static ThreadLocal<Integer> redisConnectionDbIndex = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		};
	};
	
	/*private static ThreadLocal<RedisConnectionHolder> 	redisConnectionThreadLocal = new ThreadLocal<RedisConnectionHolder>() {
		@Override
		protected RedisConnectionHolder initialValue() {
			return null;
		};
	};*/
	protected static ThreadLocal<Semaphore> updatePermition = new ThreadLocal<Semaphore>() {
		@Override
		protected Semaphore initialValue() {
			return null;
		};
	};
	private Semaphore getSempahore() {
		updatePermition.set(limitUpdate);
		return updatePermition.get();
		
	}
	protected boolean getUpdatePermition() {
		Semaphore sempahore = getSempahore();
		boolean permit = sempahore.tryAcquire(1);
		return permit;
	}
	
	protected void finishUpdate() {
		Semaphore semaphore = updatePermition.get();
		if(semaphore==null) {
			throw new ConcurrentException("semaphore==null");
		}
		final Semaphore fsemaphore = semaphore;
		new Thread(new Runnable() {
			
			Semaphore RSemaphore;
			{
				RSemaphore = fsemaphore;
			}
			
			@Override
			public void run() {
				try {
					Thread.sleep(LIMIT_TIME * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				RSemaphore.release(1);
				logCurrentTime("semaphore.release(1) finish");
			}
		}).start();
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
	
	// connection pool will cause deadlock , mast one thread one connection by RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory())
	// see :RedisTemplateFactory.getRedisConnection(redisName, dbIndex);
	/*private RedisConnection getThreadLocalRedisConnection(String redisName, int dbIndex) {
		RedisConnectionHolder redisConnectionHolder = redisConnectionThreadLocal.get();
		if(redisConnectionHolder==null||!redisName.equals(redisConnectionHolder.getServerName())) {
			RedisConnection redisConnection = RedisTemplateFactory.getRedisConnection(redisName, dbIndex);
			RedisConnectionHolder newRedisConnectionHolder = new RedisConnectionHolder(redisName, redisConnection);
			redisConnectionThreadLocal.set(newRedisConnectionHolder);
			return newRedisConnectionHolder.getRedisConnection();
		}
		return redisConnectionHolder.getRedisConnection();
	}*/
	
	/*private RedisConnection getRedisConnection() {
		RedisConnection redisConnection = redisConnectionThreadLocal.get();
		if(redisConnection==null) {
			throw new RedisConnectionException("redisConnectionThreadLocal is not init");
		}
		return redisConnection;
	}*/
	
	protected void createRedisConnection(String name, String host, int port, String password) {
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		connectionFactory.setHostName(host);
		connectionFactory.setPort(port);
		if(!StringUtils.isEmpty(password))
			connectionFactory.setPassword(password);
		connectionFactory.afterPropertiesSet();
		RedisTemplate redisTemplate = new MyStringRedisTemplate();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.afterPropertiesSet();
		RedisApplication.redisTemplatesMap.put(name, redisTemplate);
		
		Map<String, Object> redisServerMap = new HashMap<String, Object>();
		redisServerMap.put("name", name);
		redisServerMap.put("host", host);
		redisServerMap.put("port", port);
		redisServerMap.put("password", password);
		RedisApplication.redisServerCache.add(redisServerMap);
		
		initRedisKeysCache(redisTemplate, name);
		
		RedisZtreeUtil.initRedisNavigateZtree(name);
	}
	
	private void initRedisKeysCache(RedisTemplate redisTemplate, String name) {
		for(int i=0;i<=REDIS_DEFAULT_DB_SIZE;i++) {
			initRedisKeysCache(redisTemplate, name, i);
		}
	}
	
	
	protected void initRedisKeysCache(RedisTemplate redisTemplate, String serverName , int dbIndex) {
		RedisConnection connection = RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory());
		connection.select(dbIndex);
		Set<byte[]> keysSet = connection.keys("*".getBytes());
		connection.close();
		List<RKey> tempList = new ArrayList<RKey>();
		ConvertUtil.convertByteToString(connection, keysSet, tempList);
		Collections.sort(tempList);
		CopyOnWriteArrayList<RKey> redisKeysList = new CopyOnWriteArrayList<RKey>(tempList);
		if(redisKeysList.size()>0) {
			redisKeysListMap.put(serverName+DEFAULT_SEPARATOR+dbIndex, redisKeysList);
		}
	}
	
	protected static void logCurrentTime(String code) {
		if(debug) {
			System.out.println("       code:"+code+"        当前时间:" + System.currentTimeMillis());
		}
	}
}
