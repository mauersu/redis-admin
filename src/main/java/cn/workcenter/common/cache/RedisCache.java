package cn.workcenter.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import cn.workcenter.common.constant.CacheConstant;
import cn.workcenter.common.util.StringUtil;


@Component
public class RedisCache implements CacheConstant{
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}
	public String get(String key) {
		String temp = (String) redisTemplate.opsForValue().get(key);
		return temp;
				
	}
	
	public void delete(String key) {
		redisTemplate.opsForValue().getOperations().delete(key);
	}
	
	public void expireAt(String key, long millis) {
		redisTemplate.opsForValue().getOperations().expireAt(key, StringUtil.addMillis(millis));
	}
}
