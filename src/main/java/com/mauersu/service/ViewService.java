package com.mauersu.service;

import java.util.Set;

import com.mauersu.util.RKey;
import com.mauersu.util.ztree.ZNode;

public interface ViewService {

	Set<ZNode> getLeftTree();

	Set<RKey> getRedisKeys(String serverName, String dbIndex,
			String[] keyPrefixs, String queryKey, String queryValue);

	Set<ZNode> refresh();

}
