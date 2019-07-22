package com.cloud.operation.component.redis;

import java.util.Set;

public interface IJedisClient {
	
	public byte[] hget(byte[] name,byte[] key);

	public void del(byte[] byteKey);
	
	public void setex(byte[] byteKey, int timeout, byte[] serialize);

	public byte[] get(byte[] byteKey);

	public Set<byte[]> keys(String string);

	public void hdel(byte[] name,byte[] key);
	
	public void hset(byte[] bytes, byte[] bytes2, byte[] serialize);

	public Object hlen(byte[] bytes);

	public Set<byte[]> hkeys(byte[] bytes);
	
	public boolean doLock(byte[] key, int timeout, byte[] value);
	
	public void unLock(byte[] key, byte[] value);
	
	public void set(String key, String value);
	
	public String get(String key);
	
	public void del(String key);
	
	/**
	 * set相关操作
	 * @param setName
	 * @param key
	 * @param value
	 */
	public void putSet(String setName,String key,String value);
	public String getSet(String setName,String key);
	public void delSet(String setName,String... key);
	public void delSet(String setName);
	
	boolean exists(String key);
}
