package com.leo.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
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


}
