package com.cloud.operation.component.redis;

import java.util.Set;

public class JedisClient implements IJedisClient{
	
	private IJedisClient jc;
	
	private Integer lockTimeOut;
	
	public JedisClient(
			Integer maxActive, 
			Integer maxIdle, 
			Long maxWaitMillis,
			String host, 
			String port, 
			String type,
			Integer maxRedirections,
			Integer lockTimeOut,
			String password) {
		if(type.equals("single")){
			this.jc = new JedisClientImpl(maxActive, maxIdle, maxWaitMillis, host, port,lockTimeOut,password);
		}
		if(type.equals("cluster")){
			this.jc = new JedisClusterClient(maxActive, maxIdle, maxWaitMillis.intValue(), host, port, maxRedirections,password);
		}
		this.lockTimeOut = lockTimeOut;
	}

	
	@Override
    public byte[] hget(byte[] name, byte[] key) {
		return this.jc.hget(name, key);
	}

	
	@Override
    public void del(byte[] byteKey) {
		this.jc.del(byteKey);
	}

	
	@Override
    public void setex(byte[] byteKey, int timeout, byte[] serialize) {
		this.jc.setex(byteKey, timeout, serialize);
	}

	
	@Override
    public byte[] get(byte[] byteKey) {
		return this.jc.get(byteKey);
	}

	
	@Override
    public Set<byte[]> keys(String string) {
		return this.jc.keys(string);
	}

	
	@Override
    public void hdel(byte[] name, byte[] key) {
		this.jc.hdel(name, key);
	}

	
	@Override
    public void hset(byte[] bytes, byte[] bytes2, byte[] serialize) {
		this.jc.hset(bytes, bytes2, serialize);
	}

	
	@Override
    public Object hlen(byte[] bytes) {
		return this.jc.hlen(bytes);
	}

	
	@Override
    public Set<byte[]> hkeys(byte[] bytes) {
		return this.jc.hkeys(bytes);
	}


	@Override
	public boolean doLock(byte[] key, int timeout, byte[] value) {
		return this.jc.doLock(key, lockTimeOut, value);
	}


	@Override
	public void unLock(byte[] key, byte[] value) {
		this.jc.unLock(key, value);
	}
	
    @Override
    public void set(String key, String value) {
        this.jc.set(key,value);
    }
    
    @Override
    public String get(String key){
        return this.jc.get(key);
    }
    
    @Override
    public void del(String key){
        this.jc.del(key);
    }
    
    @Override
    public void putSet(String setName, String key, String value) {
        this.jc.putSet(setName, key, value);
    }


    @Override
    public void delSet(String setName, String... key) {
        this.jc.delSet(setName, key);
    }

    @Override
    public String getSet(String setName,String key){
        return this.jc.getSet(setName, key);
    }
    @Override
    public void delSet(String setName) {
        this.jc.delSet(setName);
    }
    
    @Override
    public boolean exists(String key){
        return this.jc.exists(key);
    }
	
	/**
	 * jedis.maxActive=500
jedis.maxIdle=5
jedis.maxWaitMillis=100000
jedis.host=localhost
#jedis.host=172.16.12.125:7000,172.16.12.125:7001,172.16.12.126:7000,172.16.12.126:7001,172.16.12.127:7000,172.16.12.127:7001
jedis.port=6379
jedis.maxRedirections=5000
jedis.type=single
#jedis.type=cluster
#lockTimeout 60 seconds
jedis.lockTimeOut=60
	 * @param args
	 */
	public static void main(String[] args) {
		JedisClient jc = new JedisClient(5, 5, 100000L, "172.16.2.13", "6379", "single", 5000, 60000,"security");
		System.out.println(jc.doLock("abcdef".getBytes(), 60000, "s".getBytes()));
		
	}

}
