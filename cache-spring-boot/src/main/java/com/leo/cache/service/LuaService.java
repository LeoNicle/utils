package com.leo.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class LuaService {

    @Resource
    DefaultRedisScript testRedisScript;

    @Resource
    RedisTemplate redisTemplate;

    public  boolean accessLimit(String ip, int limit, int timeout) throws IOException {

//        testRedisScript.getSha1();
        List<String> keys = Collections.singletonList(ip);
        boolean bool = (boolean)redisTemplate.execute(testRedisScript, keys, limit,timeout);

        return bool;
    }

    //一下代码封装性比较好
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public <T> T runLua(String fileClasspath, Class<T> returnType, List<String> keys, Object ... values){
        DefaultRedisScript<T> redisScript =new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(fileClasspath)));
        redisScript.setResultType(returnType);
        return stringRedisTemplate.execute(redisScript,keys,values);
    }

    //测试写法
    String pattern="yyyyMMddHHmm";
    private SimpleDateFormat simpleDateFormatThreadLocal=new SimpleDateFormat(pattern);
    //保证分布式下全局唯一id
    public String nextIDLua(){
        String key = "test"+simpleDateFormatThreadLocal.format(new Date());
        DefaultRedisScript<String> redisScript =new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("getid.lua")));
//        redisScript.setLocation(new ClassPathResource("getid.lua"));
        redisScript.setResultType(String.class);
        //System.out.println(redisScript.getSha1());
        return stringRedisTemplate.execute(redisScript,Collections.singletonList(key),"120");
    }
}
