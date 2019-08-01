package com.leo.cache.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisWithReentrantLock {
    //此处的threadlocal可以设置在baseservice接口下，或更上一层，供service使用；或单独写在一个类中，给service继承使用，static修饰
    private ThreadLocal<Map> lockers = new ThreadLocal<>();
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //真正的去设置redis
    private boolean _lock(String lockKey, String requestId, long expire, TimeUnit timeUnit){

        try{
            RedisCallback<Boolean> callback = (connection) -> {
                return connection.set(lockKey.getBytes(Charset.forName("UTF-8")), requestId.getBytes(Charset.forName("UTF-8")), Expiration.seconds(timeUnit.toSeconds(expire)), RedisStringCommands.SetOption.SET_IF_ABSENT);
            };
            return (Boolean)stringRedisTemplate.execute(callback);
        } catch (Exception e) {
//            logger.error("set redis occured an exception", e);
            System.out.println("set redis occured an exception");
        }
        return false;
    }

    private Boolean _unlock(String lockKey, String requestId) {
        RedisCallback<Boolean> callback = (connection) -> {
            return connection.eval(RedisDistributedLock.UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN ,1, lockKey.getBytes(Charset.forName("UTF-8")), requestId.getBytes(Charset.forName("UTF-8")));
        };
        return (Boolean)stringRedisTemplate.execute(callback);
    }

    public String get(String lockKey) {
        try {
            RedisCallback<String> callback = (connection) -> {
                return new String(connection.get(lockKey.getBytes()), Charset.forName("UTF-8"));
            };
            return (String)stringRedisTemplate.execute(callback);
        } catch (Exception e) {
//            logger.error("get redis occurred an exception", e);
            System.out.println("get redis occurred an exception");
        }
        return null;
    }

    //获取计数器map
    private Map <String, Integer> currentLockers() {
        Map <String, Integer> refs = lockers.get();
        if (refs != null) {
            return refs;
        }
        lockers.set(new HashMap());
        return lockers.get();
    }

    //对外提供的锁方法
    public boolean lock(String key,String requestId, long expire, TimeUnit timeUnit) {
        Map<String,Integer> refs = currentLockers();
        Integer refCnt = refs.get(key);
        if (refCnt != null) {
            refs.put(key, refCnt + 1);
            return true;
        }
        boolean ok = this._lock(key,requestId,expire,timeUnit);
        if (!ok) {
            return false;
        }
        refs.put(key, 1);
        return true;
    }

    //对外提供的解锁方法
    public boolean unlock(String key,String requestId) {
        Map<String,Integer> refs = currentLockers();
        Integer refCnt = refs.get(key);
        if (refCnt == null) {
            return false;
        }
        refCnt -= 1;
        if (refCnt > 0) {
            refs.put(key, refCnt);
        } else {
            refs.remove(key);
            this ._unlock(key,requestId);
        }
        return true;
    }

    public Integer getcount(String key){
        Map<String,Integer> map = this.lockers.get();
        map.get(key);
        if(map!=null)
            return map.get(key)==null?0:map.get(key);
        return 0;
    }
//    public static void main(String[] args) {
//        Jedis jedis = new Jedis();
//        RedisWithReentrantLock redis = new RedisWithReentrantLock(jedis);
//        System.out.println(redis.lock("codehole"));
//        System.out.println(redis.lock("codehole"));
//        System.out.println(redis.unlock("codehole"));
//        System.out.println(redis.unlock("codehole"));
//    }
}