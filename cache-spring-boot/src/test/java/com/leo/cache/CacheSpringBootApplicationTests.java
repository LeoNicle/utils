package com.leo.cache;

import com.leo.cache.entity.Employee;
import com.leo.cache.mapper.EmployeeMapper;
import com.leo.cache.service.LuaService;
import com.leo.cache.utils.RedisDistributedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheSpringBootApplicationTests {

    @Autowired
    RedisTemplate<Object, Employee> employeeRedisTemplate;

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    LuaService luaService;

    @Autowired
    RedisDistributedLock redisDistributedLock;

    @Test
    public void contextLoads() {
        Employee emp = employeeMapper.getEmpById(1);
        employeeRedisTemplate.opsForValue().set("demo-01",emp);
    }

    @Test
    public  void test1(){
        DefaultRedisScript<String> rs = new DefaultRedisScript<String>();
        //设置脚本
        rs.setScriptText("return 'Hello Redis' ");
        //定义返回类型。注意如果没有这个定义，spring不会返回结果
        rs.setResultType(String.class);

        String sha1 = rs.getSha1();
        System.out.println(sha1);
        String str = (String)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.evalSha(sha1, ReturnType.STATUS, 0, "".getBytes());
            }
        });
        System.out.println(str);


//        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
//        String str = (String)redisTemplate.execute(rs,stringRedisSerializer,stringRedisSerializer,null);
//        System.out.println(str);
    }

    @Test
    public void test2(){
        //定义lua脚本：判断两个字符串是否相同
          /*
            redis.call ('set', KEYS[1], ARGV[1])
            redis.call ('set', KEYS[2], ARGV[2])
            local str1 = redis.call ('get', KEYS [1])
            local str2 = redis.call ('get', KEYS [2])
            if str1 == str2 then
            return 1
            end
            return 0
        */
        //注意脚本中KYS[l］和KYS[2］ 的写法，它们代表客户端传递的第一个键和第二个键，
        //而ARGV[l］和ARGV[2］则表示客户端传递的第一个和第二个参数

        String lua = "redis.call ('set', KEYS[1], ARGV[1]) \n"
                + "redis.call ('set', KEYS[2], ARGV[2]) \n "
                + " local str1 = redis.call ('get', KEYS [1]) \n "
                + " local str2 = redis.call ('get', KEYS [2]) \n "
                + " if str1 == str2 then \n "
                + " return 1 \n "
                + " end \n "
                + " return 0 \n ";
        System.out.println(lua);

        DefaultRedisScript<Long> rs = new DefaultRedisScript<Long>();
        //设置脚本
        rs.setScriptText(lua);
        //定义返回类型。注意如果没有这个定义，spring不会返回结果
        rs.setResultType(Long.class);
        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();

        //定义key
        List<String> keyList = new ArrayList<>();
        keyList.add("key1");
        keyList.add("key2");
        Long restult = (Long)redisTemplate.execute(rs,stringRedisSerializer,
                stringRedisSerializer,keyList,"value1","value1");
        System.out.println(restult==1);
    }

    @Test
    public void test3() throws Exception {
        while(true){
            boolean b = luaService.accessLimit("172.16.2.13", 5, 20);
            System.out.println(b);
            TimeUnit.SECONDS.sleep(1);
        }

    }

    @Test
    public void test4(){
        List<String> keyList = new ArrayList<>();
        keyList.add("test4-1");
        keyList.add("test4-2");
        Long l = luaService.runLua("test.lua", Long.class, keyList, "value41", "value42");
        System.out.println(l.intValue()==1);
    }

    @Test
    public void getid() throws Exception{
        Integer threadSize =5;
        final CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(int i =0 ;i<20;i++){
                    System.out.println(Thread.currentThread().getName()+":"+luaService.nextIDLua());
                }
                countDownLatch.countDown();
            }
        };
        for(int i =0;i<threadSize;i++){
            new Thread(runnable,"线程"+i).start();
        }
        countDownLatch.await();
        System.out.println("end");
    }

    static String value=null ;

    @Test
    public void testlock() throws Exception{
        Integer threadSize =5;
        final CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(int i =0 ;i<5;i++){
                    if(redisDistributedLock.setLock("lockKey",Thread.currentThread().getName(),5,TimeUnit.SECONDS)) {
                        System.out.println(Thread.currentThread().getName() + "得到了锁");
                        value = redisDistributedLock.get("lockKey");
                        //解锁
                        boolean result = redisDistributedLock.releaseLock("lockKey", value);
                        System.out.println(value+"解锁："+result);
                    }else{
                        System.out.println(Thread.currentThread().getName()+"没有得到锁");
                    }
                }
                countDownLatch.countDown();
            }
        };
        for(int i =0;i<threadSize;i++){
            new Thread(runnable,"线程"+i).start();
        }
        countDownLatch.await();
        //超期解锁测试
        try {
            TimeUnit.SECONDS.sleep(10);
        }catch (InterruptedException e){

        }
        //
        if(value==null){
            throw new RuntimeException("value is null");
        }
        System.out.println("===============");
        System.out.println(value);
        boolean result = redisDistributedLock.releaseLock("lockKey", value);
        System.out.println("超期解锁："+result);
        System.out.println("end");
        redisDistributedLock.get("lockKey");
    }
}
