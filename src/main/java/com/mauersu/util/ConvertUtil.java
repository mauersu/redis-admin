package com.mauersu.util;

import java.nio.charset.Charset;
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
}
