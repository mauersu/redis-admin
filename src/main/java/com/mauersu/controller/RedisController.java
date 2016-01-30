package com.mauersu.controller;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.workcenter.common.WorkcenterResult;
import cn.workcenter.common.response.WorkcenterResponseBodyJson;
import cn.workcenter.common.util.StringUtil;

import com.mauersu.service.RedisService;
import com.mauersu.service.ViewService;
import com.mauersu.util.Constant;
import com.mauersu.util.ConvertUtil;
import com.mauersu.util.QueryEnum;
import com.mauersu.util.RKey;
import com.mauersu.util.RedisApplication;
import com.mauersu.util.ztree.RedisZtreeUtil;
import com.mauersu.util.ztree.ZNode;

@Controller
@RequestMapping("/redis")
public class RedisController extends RedisApplication implements Constant{
	
	@Autowired
	private ViewService viewService;
	@Autowired
	private RedisService redisService;
	
	@RequestMapping(method=RequestMethod.GET)
	public Object home(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("basePath", BASE_PATH);
		request.setAttribute("viewPage", "home.jsp");
		String defaultServerName = (String) (RedisApplication.redisServerCache.get(0)==null?"":RedisApplication.redisServerCache.get(0).get("name"));
		request.setAttribute("serverName", defaultServerName);
		request.setAttribute("dbIndex", DEFAULT_DBINDEX);
		return "admin/main";
	}
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public Object index(HttpServletRequest request, HttpServletResponse response) {
		
		request.setAttribute("basePath", BASE_PATH);
		request.setAttribute("viewPage", "home.jsp");
		
		String defaultServerName = (String) (RedisApplication.redisServerCache.get(0)==null?"":RedisApplication.redisServerCache.get(0).get("name"));
		request.setAttribute("serverName", defaultServerName);
		request.setAttribute("dbIndex", DEFAULT_DBINDEX);
		return "admin/main";
	}
	
	@RequestMapping(value="/addServer", method=RequestMethod.POST)
	@ResponseBody
	public Object addServer(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String host, 
			@RequestParam String name,
			@RequestParam int port,
			@RequestParam String password) {
		
		redisService.addRedisServer(name, host, port, password);
		
		return WorkcenterResponseBodyJson.custom().build();
	}
	
	@RequestMapping(value="/serverTree", method=RequestMethod.GET)
	@ResponseBody
	public Object serverTree(HttpServletRequest request, HttpServletResponse response) {
		
		Set<ZNode> keysSet = viewService.getLeftTree();
		
		return keysSet;
	}
	
	@RequestMapping(value="/refresh", method=RequestMethod.GET)
	@ResponseBody
	public Object refresh(HttpServletRequest request, HttpServletResponse response) {
		logCurrentTime("viewService.refresh(); start");
		viewService.refresh();
		logCurrentTime("viewService.refresh(); end");
		return WorkcenterResponseBodyJson.custom().build();
	}
	
	private void refreshByMode() {
		switch(refreshMode) {
		case manually:
			break;
		case auto:
			viewService.refresh();
			break;
		}
	}
	
	@RequestMapping(value="/refreshMode", method=RequestMethod.POST)
	@ResponseBody
	public Object refreshMode(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String mode) {
		
		viewService.changeRefreshMode(mode);
		
		return WorkcenterResponseBodyJson.custom().build();
	}
	
	@RequestMapping(value="/changeShowType", method=RequestMethod.POST)
	@ResponseBody
	public Object changeShowType(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String state) {
		
		viewService.changeShowType(state);
		return WorkcenterResponseBodyJson.custom().build();
	}
	
	@RequestMapping(value="/stringList/{serverName}/{dbIndex}", method=RequestMethod.GET)
	public Object stringList(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String serverName, @PathVariable String dbIndex) {
		
		refreshByMode();
		
		String queryKey = StringUtil.getParameterByDefault(request, "queryKey", MIDDLE_KEY);
		String queryKey_ch = QueryEnum.valueOf(queryKey).getQueryKeyCh();
		String queryValue = StringUtil.getParameterByDefault(request, "queryValue", EMPTY_STRING);
		
		String[] keyPrefixs = request.getParameterValues("keyPrefixs");
		logCurrentTime("viewService.getRedisKeys start");
		Set<RKey> redisKeys = viewService.getRedisKeys(serverName, dbIndex, keyPrefixs, queryKey, queryValue);
		logCurrentTime("viewService.getRedisKeys end");
		request.setAttribute("redisServers", redisServerCache);
		request.setAttribute("basePath", BASE_PATH);
		request.setAttribute("queryLabel_ch", queryKey_ch);
		request.setAttribute("queryLabel_en", queryKey);
		request.setAttribute("queryValue", queryValue);
		request.setAttribute("serverName", serverName);
		request.setAttribute("dbIndex", dbIndex);
		request.setAttribute("redisKeys", redisKeys);
		request.setAttribute("refreshMode", refreshMode.getLabel());
		request.setAttribute("change2ShowType", showType.getChange2());
		request.setAttribute("showType", showType.getState());
		request.setAttribute("viewPage", "redis/list.jsp");
		return "admin/main";
	}
	
	@RequestMapping(value="/KV", method=RequestMethod.POST)
	@ResponseBody
	public Object updateKV(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam String serverName, @RequestParam int dbIndex, 
			@RequestParam String dataType, 
			@RequestParam String key) {
		
		String[] value = request.getParameterValues("value");
		double[] score = ConvertUtil.convert2Double(request.getParameterValues("score"));
		String[] member = request.getParameterValues("member");
		String[] field = request.getParameterValues("field");
		
		redisService.addKV(serverName, dbIndex, dataType, key, value, score, member, field);
		
		return WorkcenterResponseBodyJson.custom().build();
	}
	
	@RequestMapping(value="/KV", method=RequestMethod.GET)
	@ResponseBody
	public Object getKV(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam String serverName, @RequestParam int dbIndex, 
			@RequestParam String dataType, 
			@RequestParam String key) {
		
		WorkcenterResult result = (WorkcenterResult)redisService.getKV(serverName, dbIndex, dataType, key);
		
		return WorkcenterResponseBodyJson.custom().setAll(result, GETKV).build();
	}
	
	@RequestMapping(value="/delKV", method=RequestMethod.POST) 
	@ResponseBody
	public Object delKV(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam String serverName, @RequestParam int dbIndex, 
			@RequestParam String deleteKeys) {
		
		redisService.delKV(serverName, dbIndex, deleteKeys);
		
		return WorkcenterResponseBodyJson.custom().build();
		
	}
}
