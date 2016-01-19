package com.mauersu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mauersu.exception.RedisInitException;
import com.mauersu.util.ztree.RedisZtreeUtil;

@Service
@SuppressWarnings("rawtypes")
public class InitContext extends RedisApplication implements Constant  {

	@Autowired
	private Environment env;
	
	@PostConstruct
	public void initRedisServers() {
		try {
			int serverNum = Integer.parseInt(env.getProperty(REDISPROPERTIES_SERVER_NUM_KEY));
			for(int i=1;i<=serverNum;i++) {
				String name = env.getProperty(REDISPROPERTIES_NAME_PROFIXKEY + i);
				String host = env.getProperty(REDISPROPERTIES_HOST_PROFIXKEY + i);
				int port = Integer.parseInt(env.getProperty(REDISPROPERTIES_PORT_PROFIXKEY + i));
				String password = env.getProperty(REDISPROPERTIES_PASSWORD_PROFIXKEY + i);
				
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
				
				initRedisKeysCache(redisTemplate, name, 0);
				
				RedisZtreeUtil.initRedisNavigateZtree(name);
				
				runUpdateLimit();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new RedisInitException(e);
		}
	}

	public void initRedisKeysCache(RedisTemplate redisTemplate, String serverName , int dbIndex) {
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
