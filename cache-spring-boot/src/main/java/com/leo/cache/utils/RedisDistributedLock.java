package com.leo.cache.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDistributedLock {
    private static final Long SUCCESS = 1L;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

    public boolean setLock(String lockKey, String requestId, long expire, TimeUnit timeUnit) {
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

    /**
     * 释放锁
     * @param lockKey
     * @param requestId 唯一ID
     * @return
     */
    public boolean releaseLock(String lockKey, String requestId) {
        RedisCallback<Boolean> callback = (connection) -> {
            return connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN ,1, lockKey.getBytes(Charset.forName("UTF-8")), requestId.getBytes(Charset.forName("UTF-8")));
        };
        return (Boolean)stringRedisTemplate.execute(callback);
    }

    /**
     * 获取Redis锁的value值
     * @param lockKey
     * @return
     */
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


    //以下是以纯lua方式做的分布式锁
    /**  private static final Long SUCCESS = 1L;

     /**
     * 获取锁
     * @param lockKey
     * @param value
     * @param expireTime：单位-秒
     * @return
     *
    public static boolean getLock(StringRedisTemplate stringRedisTemplate,String lockKey, String value, int expireTime){
    boolean ret = false;
    try{
    String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";

    RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);

    Object result = stringRedisTemplate.execute(redisScript, Collections.singletonList(lockKey),value,expireTime);

    if(SUCCESS.equals(result)){
    return true;
    }

    }catch(Exception e){

    }
    return ret;
    }

    /**
     * 释放锁
     * @param lockKey
     * @param value
     * @return
     *
    public static boolean releaseLock(StringRedisTemplate stringRedisTemplate,String lockKey, String value){

    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);

    Object result = stringRedisTemplate.execute(redisScript, Collections.singletonList(lockKey),value);
    if(SUCCESS.equals(result)) {
    return true;
    }

    return false;
    }
     */
}
