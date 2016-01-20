package com.mauersu.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class ConvertUtil {

	public static void convertByteToString(RedisConnection connection, Set<byte[]> keysSet, List<RKey> tempList) {
		StringRedisSerializer stringSerializer = new StringRedisSerializer(Charset.forName("UTF8"));
		for(byte[] byteArray: keysSet) {
			String converted = stringSerializer.deserialize(byteArray);
			DataType dateType = connection.type(byteArray);
			RKey rkey = new RKey(converted, dateType); 
			tempList.add(rkey);
		}
	}
	
	public static Double[] convert2Double(String[] strings) {
		if(strings==null) return null;
		List<Double> doubleList = new ArrayList<Double>();
		for(String str: strings) {
			Double d = Double.parseDouble(str);
			doubleList.add(d);
		}
		return (Double[]) doubleList.toArray();
	}
}
