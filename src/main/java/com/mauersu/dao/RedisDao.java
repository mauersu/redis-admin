package com.mauersu.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.mauersu.util.RValue;
import com.mauersu.util.RedisApplication;

@Service
public class RedisDao extends RedisApplication {
	
	@Autowired
	RedisTemplateFactory redisTemplateFactory;
	
	//--- SET 
	public void addSTRING(String serverName, int dbIndex, String key,
			String value) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisTemplate.opsForValue().set(key, value);
	}
	
	public void addLIST(String serverName, int dbIndex, String key,
			String[] values) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisTemplate.opsForList().rightPushAll(key, values);
	}
	
	public void addSET(String serverName, int dbIndex, String key,
			String[] values) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisTemplate.opsForSet().add(key, values);
	}

	public void addZSET(String serverName, int dbIndex, String key,
			Double[] scores, String[] members) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		Set<TypedTuple<Object>> zset = new HashSet<TypedTuple<Object>>();
		for(int i=0;i<members.length;i++) {
			final Object ob = members[i];
			final Double sc = scores[i];
			zset.add(new TypedTuple () {
				private Object v;
				private Double score;
				{
					v = ob;
					score = sc;
				}
				@Override
				public int compareTo(Object o) {
					if(o == null) return 1;
					if(o instanceof TypedTuple) {
						TypedTuple tto = (TypedTuple) o;
						return this.getScore()-tto.getScore() >= 0?1:-1;
					}
					return 1;
				}
				@Override
				public Object getValue() {
					return v;
				}
				@Override
				public Double getScore() {
					return score;
				}
			});
		}
		redisTemplate.opsForZSet().add(key, zset);
	}
	
	public void addHASH(String serverName, int dbIndex, String key,
			String[] fields, String[] values) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		Map<String, String> hashmap = new HashMap<String, String>();
		
		for(int i=0;i<fields.length;i++) {
			String field = fields[i];
			String value = values[i];
			hashmap.put(field, value);
		}
		redisTemplate.opsForHash().putAll(key, hashmap);
	}

	//--- GET 
	public Object getSTRING(String serverName, int dbIndex, String key) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		final Object value = redisTemplate.opsForValue().get(key);
		List list = new ArrayList();
		list.add(new RValue(value));
		return list;
	}
	
	public Object getLIST(String serverName, int dbIndex, String key) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		List<Object> values = redisTemplate.opsForList().range(key, 0, 1000);
		return RValue.creatListValue(values);
	}

	public Object getSET(String serverName, int dbIndex, String key) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		Set<Object> values = redisTemplate.opsForSet().members(key);
		return RValue.creatSetValue(values);
	}

	public Object getZSET(String serverName, int dbIndex, String key) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		Set<TypedTuple<Object>> values = redisTemplate.opsForZSet().rangeWithScores(key, 0, 1000);
		return RValue.creatZSetValue(values);
	}

	public Object getHASH(String serverName, int dbIndex, String key) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		Map<Object, Object> values = redisTemplate.opsForHash().entries(key);
		return RValue.creatHashValue(values);
	}

	//--- delete
	public void delRedisKeys(String serverName, int dbIndex, String deleteKeys) {
		RedisTemplate<String, Object> redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		String[] keys = deleteKeys.split(",");
		redisTemplate.delete(Arrays.asList(keys));
		return;
	}
}
