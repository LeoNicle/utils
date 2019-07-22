package com.cloud.operation.component.redis;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClientImpl implements IJedisClient{
	
	private JedisPool jedisPool;
	
	public JedisClientImpl(
		Integer maxActive, 
		Integer maxIdle, 
		Long maxWaitMillis,
		String host, 
		String port,
		Integer timeout,
		String password) {
		//配置连接池
		JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(true);
        
		//jedisPool = new JedisPool(config, host, Integer.parseInt(port));
		jedisPool =new JedisPool(config, host, Integer.parseInt(port), timeout, password);
	}
	
	/***
	 * 获取jedis对象
	 * @return
	 */
	public synchronized Jedis getJedis(){
		Jedis jedis = null;
        if (jedisPool != null) {  
            jedis = jedisPool.getResource(); 
        }
        return jedis;
	}
	
	/***
	 * 返回资源
	 * @param jedis
	 */
	public void returnResource(Jedis jedis) {
	    if (jedis != null && jedisPool !=null) {
	    	jedisPool.returnResource(jedis);
		}
	}
	
	@Override
    public byte[] hget(byte[] name,byte[] key){
		Jedis jedis = jedisPool.getResource();
		byte[] str = jedis.hget(name, key);
		jedisPool.returnResource(jedis);
		return str;
	}

	@Override
    public void del(byte[] byteKey) {
		Jedis jedis = getJedis();
		jedis.del(byteKey);
		returnResource(jedis);
	}
	
	@Override
    public void setex(byte[] byteKey, int timeout, byte[] serialize) {
		Jedis jedis = getJedis();
		jedis.setex(byteKey, timeout, serialize);
		returnResource(jedis);
	}

	@Override
    public byte[] get(byte[] byteKey) {
		Jedis jedis = getJedis();
		byte[] ret = jedis.get(byteKey);
		returnResource(jedis);
		return ret;
	}

	@Override
    public Set<byte[]> keys(String string) {
		Jedis jedis = getJedis();
		Set<byte[]> ret = jedis.keys(string.getBytes());
		returnResource(jedis);
		return ret;
	}

	@Override
    public void hdel(byte[] name,byte[] key){
		Jedis jedis = jedisPool.getResource();
		jedis.hdel(name, key);
		jedisPool.returnResource(jedis);
	}
	
	@Override
    public void hset(byte[] bytes, byte[] bytes2, byte[] serialize) {
		Jedis jedis = getJedis();
		jedis.hset(bytes, bytes2, serialize);
		returnResource(jedis);
	}
	

	@Override
    public Object hlen(byte[] bytes) {
		return getJedis().hlen(bytes);
	}

	@Override
    public Set<byte[]> hkeys(byte[] bytes) {
		return getJedis().hkeys(bytes);
	}

	@Override
	public boolean doLock(byte[] key, int timeout, byte[] value) {
		String result = getJedis().setnx(key, value).toString();
		if(result.equals("1")){
			getJedis().expire(key, timeout);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void unLock(byte[] key, byte[] value) {
		getJedis().del(key);
	}
	
    @Override
    public void set(String key, String value) {
        getJedis().set(key, value);
    }
    
    @Override
    public String get(String key){
        return getJedis().get(key);
    }
    
    @Override
    public void del(String key){
        getJedis().del(key);
    }
    
    @Override
    public void putSet(String setName,String key,String value){
        Jedis jedis = getJedis();
        jedis.hset(setName, key, value);
        returnResource(jedis);
    }
    
    @Override
    public String getSet(String setName,String key){
        Jedis jedis = getJedis();
        String value = jedis.hget(setName, key);
        returnResource(jedis);
        return value;
    }
    
    @Override
    public void delSet(String setName,String... key){
        Jedis jedis = getJedis();
        jedis.hdel(setName, key);
        returnResource(jedis);
    }
    @Override
    public void delSet(String setName){
        Jedis jedis = getJedis();
        jedis.hdel(setName);
        returnResource(jedis);
    }
    
    @Override
    public boolean exists(String key){
        Jedis jedis = getJedis();
        try{
            return jedis.exists(key);
        }finally{
            returnResource(jedis);
        }
    }
}
