package com.mauersu.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mauersu.dao.RedisTemplateFactory;
import com.mauersu.service.ViewService;
import com.mauersu.util.Constant;
import com.mauersu.util.RKey;
import com.mauersu.util.RedisApplication;
import com.mauersu.util.ztree.RedisZtreeUtil;
import com.mauersu.util.ztree.ZNode;

@Service
public class ViewServiceImpl extends RedisApplication implements ViewService, Constant {

	@Autowired
	public void setRedisTemplateFactory(RedisTemplateFactory redisTemplateFactory) {
		this.redisTemplateFactory = redisTemplateFactory;
	}

	@Override
	public Set<ZNode> getLeftTree() {
		return getLeftTree(DEFAULT_REDISSERVERNAME, DEFAULT_DBINDEX);
	}

	public Set<ZNode> getLeftTree(String redisName, int dbIndex) {
		return getLeftTree(redisName, dbIndex, useVMCache);
	}
	
	private Set<ZNode> getLeftTree(String redisName, int dbIndex, boolean useVMCache) {
		if(!useVMCache) {
			//not finish
			return null;
		}
		RedisZtreeUtil.initRedisNavigateZtree(redisName, dbIndex);
		return new TreeSet<ZNode>(redisNavigateZtree);
	}
	
	@Override
	public Set<ZNode> refresh() {
		boolean permit = getUpdatePermition();
		Set<ZNode> zTree = null;
		if(permit) {
			try {
				for(Map<String, Object> redisServerMap : RedisApplication.redisServerCache) {
					refreshKeys((String)redisServerMap.get("name"), DEFAULT_DBINDEX);
					zTree = refreshServerTree((String)redisServerMap.get("name"), DEFAULT_DBINDEX);
					// test limit flow System.out.println("yes permit");
				}
			} finally {
				finishUpdate();
			}
		} else {
			// test limit flow System.out.println("no permit");
		}
		return zTree;
	}
	
	private void refreshKeys(String serverName, int dbIndex) {
		RedisTemplate redisTemplate = RedisApplication.redisTemplatesMap.get(serverName);
		initRedisKeysCache(redisTemplate, serverName, dbIndex);
	}

	private Set<ZNode> refreshServerTree() {
		return refreshServerTree(DEFAULT_REDISSERVERNAME, DEFAULT_DBINDEX);
	}
	
	private Set<ZNode> refreshServerTree(String serverName,
			int dbIndex) {
		 RedisZtreeUtil.refreshRedisNavigateZtree(serverName, dbIndex) ;
		 return new TreeSet<ZNode>(redisNavigateZtree);
	}

	@Override
	public Set<RKey> getRedisKeys(String serverName, String dbIndex, String[] keyPrefixs, String queryKey, String queryValue) {
		List<RKey> allRedisKeys = redisKeysListMap.get(serverName + dbIndex);
		if(keyPrefixs == null || keyPrefixs.length == 0) {
			if(StringUtils.isEmpty(queryKey) && StringUtils.isEmpty(queryValue)) {
				return new TreeSet<RKey>(allRedisKeys);
			}
			Set<RKey> queryRedisKeys = getQueryRedisKeys(allRedisKeys, queryKey, queryValue);
			return queryRedisKeys;
		}
		StringBuffer keyPrefix = new StringBuffer("");
		for(String prefix: keyPrefixs) {
			keyPrefix.append(prefix).append(DEFAULT_REDISKEY_SEPARATOR);
		}
		Set<RKey> conformRedisKeys = getConformRedisKeys(allRedisKeys, keyPrefix.toString());
		return conformRedisKeys;
	}

	private Set<RKey> getQueryRedisKeys(List<RKey> allRedisKeys, String queryKey, String queryValue) {
		TreeSet<RKey> rKeySet = new TreeSet<RKey>();
		for(RKey rKey : allRedisKeys) {
			switch(queryKey) {
			case MIDDLE_KEY:
				if(rKey.contains(queryValue)) {
					rKeySet.add(rKey);
				}
				break;
			case HEAD_KEY:
				if(rKey.startsWith(queryValue)) {
					rKeySet.add(rKey);
				}
				break;
			case TAIL_KEY:
				if(rKey.endsWith(queryValue)) {
					rKeySet.add(rKey);
				}
				break;
			}
		}
		return rKeySet;
	}

	private Set<RKey> getConformRedisKeys(List<RKey> allRedisKeys, String keyPrefix) {
		TreeSet<RKey> rKeySet = new TreeSet<RKey>();
		for(RKey rKey : allRedisKeys) {
			if(rKey.startsWith(keyPrefix)) {
				rKeySet.add(rKey);
			}
		}
		return rKeySet;
	}

}
