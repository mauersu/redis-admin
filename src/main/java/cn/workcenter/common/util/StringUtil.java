package cn.workcenter.common.util;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import cn.workcenter.common.exception.ParameterEmptyException;

public class StringUtil {
	
	public static void main(String[] args) {
		System.out.println(getRandom(6));
	}
	
	public static boolean isEmpty(String str){
		
		return ("".equals(str)||str == null) ;
	}
	
	public static String getRandom(int num) {
		String random = "";
		if(num<=0) 
			return random;
		for(int i=0;i<num;i++) {
			random += getRandom();
		}
		return random;
	}
	
	public static String getRandom(){
		return (int)(Math.random()*10)+"";
	}

	public static String getString(Map<String, Object> map, String key) {
		return getString(map, key, "");
	}
	
	public static String getString(Map<String, Object> map, String key, String defaultValue) {
		String value = (map.get(key)==null?"":map.get(key)).toString();
		value = StringUtils.isEmpty(value) ? "" : value;
		return value;
	}
	
	public static Date addMillis(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis() + millis);
		return calendar.getTime();

	}

	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉"-"符号
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
	}

	public static String getParameterExceptionEmpty(HttpServletRequest request, String key) {
		String value = request.getParameter(key);
		if(isEmpty(value)) {
			throw new ParameterEmptyException(key + "-empty is invalid!");
		}
		return value;
	}
	
	public static String getParameterByDefault(HttpServletRequest request, String key, String defaultValue) {
		String value = request.getParameter(key);
		if(isEmpty(value)) {
			return defaultValue;
		}
		return value;
	}
	
	public final static String MD5(String s) {
		 char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		 try {
			 byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			 MessageDigest mdInst = MessageDigest.getInstance("MD5");
			 // 使用指定的字节更新摘要
			 mdInst.update(btInput);
			// 获得密文
			 byte[] md = mdInst.digest();
			 
			 int j = md.length;
			 char str[] = new char[j*2];
			 int k = 0;
			 for(int i=0;i<j;i++) {
				 byte byte0 = md[i];
				 str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				 str[k++] = hexDigits[byte0 & 0xf];
			 }
			 
			 return new String(str);
		 } catch(Exception e) {
			 e.printStackTrace();
			 return null;
		 }
	}
}
