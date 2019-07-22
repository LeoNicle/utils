package com.cloud.operation.component.redis;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClusterClient implements IJedisClient{
	
	public static final Logger logger = LoggerFactory.getLogger(JedisClusterClient.class);
	
	private BinaryJedisCluster jc;
	
	public JedisClusterClient(
			Integer maxActive, 
			Integer maxIdle, 
			Integer maxWaitMillis,
			String host, 
			String port, 
			Integer maxRedirections,
			String password) {
		String[] hosts = host.split(",");
		Set<HostAndPort> hps = new HashSet<HostAndPort>();  
		for(String hostp : hosts){
			HostAndPort hap = new HostAndPort(hostp.split(":")[0], Integer.parseInt(hostp.split(":")[1]));  
			hps.add(hap);
		}
		JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(true);
        //this.jc = new BinaryJedisCluster(hps, maxWaitMillis, maxRedirections, config);
        this.jc =new BinaryJedisCluster(hps, maxWaitMillis, 60000 , maxRedirections, password, config);
	}
	
	private synchronized BinaryJedisCluster getCluster(){
		return this.jc;
	}
	
	@Override
	public byte[] hget(byte[] name, byte[] key) {
		BinaryJedisCluster jc = getCluster();
		return jc.hget(name, key);
	}
	@Override
	public void del(byte[] byteKey) {
		BinaryJedisCluster jc = getCluster();
		jc.del(byteKey);
	}
	@Override
	public void setex(byte[] byteKey, int timeout, byte[] serialize) {
		BinaryJedisCluster jc = getCluster();
		jc.setex(byteKey, timeout, serialize);
	}
	@Override
	public byte[] get(byte[] byteKey) {
		BinaryJedisCluster jc = getCluster();
		return (jc.get(byteKey));
	}
	@Override
	public Set<byte[]> keys(String string) {
		BinaryJedisCluster jc = getCluster();
		Set<byte[]> keys = jc.hkeys(string.getBytes());
		return keys;
	}
	@Override
	public void hdel(byte[] name, byte[] key) {
		BinaryJedisCluster jc = getCluster();
		jc.hdel(name, key);
	}
	@Override
	public void hset(byte[] bytes, byte[] bytes2, byte[] serialize) {
		BinaryJedisCluster jc = getCluster();
		jc.hset(bytes, bytes2, serialize);
	}
	@Override
	public Object hlen(byte[] bytes) {
		BinaryJedisCluster jc = getCluster();
		return jc.hlen(bytes);
	}
	@Override
	public Set<byte[]> hkeys(byte[] bytes) {
		BinaryJedisCluster jc = getCluster();
		Set<byte[]> keys = jc.hkeys(bytes);
		return keys;
	}

	@Override
	public boolean doLock(byte[] key, int timeout, byte[] value) {
		String result = jc.setnx(key, value).toString();
		if(result.equals("1")){
			jc.expire(key, timeout);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void unLock(byte[] key, byte[] value) {
		BinaryJedisCluster jc = getCluster();
		jc.del(key);
	}

    @Override
    public void set(String key, String value) {
        BinaryJedisCluster jc = getCluster();
        try {
            jc.set(key.getBytes("utf-8"), value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String key) {
        BinaryJedisCluster jc = getCluster();
        try {
            byte[] value = jc.get(key.getBytes("utf-8"));
            return new String(value,"utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    @Override
    public void del(String key){
        try {
            del(key.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void putSet(String setName,String key,String value){
        BinaryJedisCluster jc = getCluster();
        try {
            jc.hset(setName.getBytes("utf-8"), key.getBytes("utf-8"), value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String getSet(String setName,String key){
        BinaryJedisCluster jc = getCluster();
        try {
            return new String(jc.hget(setName.getBytes("utf-8"), key.getBytes("utf-8")),"utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void delSet(String setName,String... keys){
        byte[][] kbs = new byte[keys.length][];
        try {
            for(int i=0;i<keys.length;i++){
                String key = keys[i];
                kbs[i] = key.getBytes("utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        
        try {
            jc.hdel(setName.getBytes("utf-8"), kbs);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void delSet(String setName){
        BinaryJedisCluster jc = getCluster();
        try {
            jc.hdel(setName.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean exists(String key){
        BinaryJedisCluster jc = getCluster();
        try{
            return jc.exists(key.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
