package com.mauersu.dao;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mauersu.exception.RedisConnectionException;
import com.mauersu.util.RedisApplication;

@Service
public class RedisTemplateFactory extends RedisApplication {
	
	protected RedisTemplate<String, Object> getRedisTemplate(String redisName) {
		RedisTemplate<String, Object> redisTemplate = redisTemplatesMap.get(redisName);
		if(redisTemplate==null) {
			throw new RedisConnectionException("had not connected to " + redisName + " this redis server now.");
		}
		return redisTemplate;
	}
	
	private void validate(int dbIndex) {
		if(0> dbIndex || dbIndex> 15) {
			throw new RedisConnectionException("redis dbIndex is invalid : " + dbIndex);
		}
		return ;
	}
	
	public RedisConnection getRedisConnection(String redisName, int dbIndex) {
		RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisName);
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		validate(dbIndex);
		connection.select(dbIndex);
		return connection;
	}
	
	public RedisConnection getRedisConnection(String redisName) {
		RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisName);
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		return connection;
	}
	
}
