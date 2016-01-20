package com.mauersu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.workcenter.common.WorkcenterCodeEnum;
import cn.workcenter.common.WorkcenterResult;
import cn.workcenter.common.constant.WebConstant;

import com.mauersu.dao.RedisDao;
import com.mauersu.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService, WebConstant {
	@Autowired
	private RedisDao redisDao;
	@Override
	public void addKV(String serverName, int dbIndex, String dataType,
			String key, 
			String[] values, Double[] scores, String[] members, String[] fields) {
		
		switch(dataType) {
		case "STRING":
			redisDao.addSTRING(serverName, dbIndex, key, values[0]);
			break;
		case "LIST":
			redisDao.addLIST(serverName, dbIndex, key, values);
			break;
		case "SET":
			redisDao.addSET(serverName, dbIndex, key, values);
			break;
		case "ZSET":
			redisDao.addZSET(serverName, dbIndex, key, scores, members);
			break;
		case "HASH":
			redisDao.addHASH(serverName, dbIndex, key, fields, values);
			break;
		}
	}
	@Override
	public WorkcenterResult getKV(String serverName, int dbIndex, String dataType, String key) {
		
		Object values = null;
		switch(dataType) {
		case "STRING":
			values = redisDao.getSTRING(serverName, dbIndex, key);
			break;
		case "LIST":
			values = redisDao.getLIST(serverName, dbIndex, key);
			break;
		case "SET":
			values = redisDao.getSET(serverName, dbIndex, key);
			break;
		case "ZSET":
			values = redisDao.getZSET(serverName, dbIndex, key);
			break;
		case "HASH":
			values = redisDao.getHASH(serverName, dbIndex, key);
			break;
		}
		
		return WorkcenterResult.custom().setOK(WorkcenterCodeEnum.valueOf(OK_REDISKV_UPDATE), values).build();
	}
	@Override
	public void delKV(String serverName, int dbIndex, String deleteKeys) {
		redisDao.delRedisKeys(serverName, dbIndex, deleteKeys);
		return;
	}
	
	
}
