package com.leo.cache;

import com.google.common.hash.Funnels;
import com.leo.cache.config.Myconfig;
import com.leo.cache.entity.Employee;
import com.leo.cache.mapper.EmployeeMapper;
import com.leo.cache.service.LuaService;
import com.leo.cache.utils.delayqueue.RedisDelayingQueue;
import com.leo.cache.utils.bloom.BloomFilterHelper;
import com.leo.cache.utils.bloom.RedisBloomFilter;
import com.leo.cache.utils.lock.RedisDistributedLock;
import com.leo.cache.utils.lock.RedisWithReentrantLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;

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

    @Autowired
    Myconfig myconfig;

    //漏斗限流--避免简单限流弊端（短时间大访问规定时，造成存储浪费）
    @Test
    public void testFunnelRateLimiter(){
//        String key = String.format("%s:%s-%s", "leoid", "leokey","leo2");组装修改字符串格式
//        System.out.println(key);
    }

    //简单限流--移动窗口，目前还未写完
    @Test
    public void testSimpleRateLimiter(){

    }


    //测试布隆过滤器

    /**
     * 布隆过滤器对于已经见过的元素肯定不会误判，它只会误判那些没见过的元
     * 素。它说见过，可能压根没见过。他说没见过，就一定没见过。
     */
    @Autowired
    private RedisBloomFilter redisBloomFilter;
    @Test
    public void testBloom(){
        BloomFilterHelper<CharSequence> bloomFilterHelper=new BloomFilterHelper<>(Funnels.stringFunnel(Charset.defaultCharset()),10000,0.1);
        int j = 0;
        for (int i = 0; i < 1000; i++) {
            redisBloomFilter.addByBloomFilter(bloomFilterHelper, "bloom", i+"");
        }
        for (int i = 1001; i <=2000; i++) {
            boolean result = redisBloomFilter.includeByBloomFilter(bloomFilterHelper, "bloom", i+"");
            if (!result) {
                j++;
            }else{
                System.out.println("见过"+i);
            }
        }
        System.out.println("有" + j + "个没见过");
    }

    //测试hyperloglog,提供不精确的去重统计，比set节省空间。
    @Test
    public void pfadd(){
        for (int i = 0; i <1000 ; i++) {
            redisTemplate.opsForHyperLogLog().add("user","user"+i);
            redisTemplate.opsForHyperLogLog().add("用户","用户"+i);
        }
        System.out.println("======");

        System.out.println(redisTemplate.opsForHyperLogLog().size("user"));
        System.out.println(redisTemplate.opsForHyperLogLog().size("用户"));
        System.out.println(redisTemplate.opsForHyperLogLog().size("user","用户"));
    }

    /**
     * 延时队列，待思考,待优化
     * @throws Exception
     */
    @Test
    public void testDelayQueue() throws Exception{
        RedisDelayingQueue<Map> mapRedisDelayingQueue = new RedisDelayingQueue<Map>("delaytest",redisTemplate,myconfig.getRedisHost(),myconfig.getRedisPort());

        for (int i = 0; i <5 ; i++) {
            HashMap map=new HashMap();
            map.put(i,"value"+i);
            mapRedisDelayingQueue.delay(map,i);
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                mapRedisDelayingQueue.loop();
            }
        };
        System.out.println("======");
        for (int i = 0; i < 5; i++) {
            new Thread(run,"线程"+i).start();
        }

        TimeUnit.SECONDS.sleep(20);
        System.out.println("end");
    }

    @Test
    public void contextLoads() {
        Employee emp = employeeMapper.getEmpById(1);
        employeeRedisTemplate.opsForValue().set("demo-01",emp);
    }

    /**
     * 简单测试lua脚本
     */
    @Test
    public  void test1(){
        DefaultRedisScript<String> rs = new DefaultRedisScript<String>();
        //设置脚本
        rs.setScriptText("return 'Hello Redis' ");
        //定义返回类型。注意如果没有这个定义，spring不会返回结果
        rs.setResultType(String.class);

//        String sha1 = rs.getSha1();
//        System.out.println(sha1);
//        String str = (String)redisTemplate.execute(new RedisCallback() {
//            @Override
//            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
//                return redisConnection.evalSha(sha1, ReturnType.STATUS, 0, "".getBytes());
//            }
//        });
//        System.out.println(str);


        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
        String str = (String)redisTemplate.execute(rs,stringRedisSerializer,stringRedisSerializer,null);
        System.out.println(str);
    }

    /**
     * lua-redis案例
     * 定义lua脚本：判断两个字符串是否相同
     */
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

    /**
     * lua脚本方式实现限流-
     * 弊端：当规定时间内没有超，但是第5秒请求了2次（假定规定是5秒内5次），第六秒
     * 请求了3次，第七秒请求到达时是可以，但是这样的话，5和6秒已经达到规定，7秒却可以访问，不太符合。
     * 这个相当于把时间分成一个一个的时间段。不如使用zset做的简单移动窗口限流器
     * @throws Exception
     */
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

    //测试重入分布式锁
    @Autowired
    RedisWithReentrantLock redisWithReentrantLock;
    @Test
    public void testReLock() throws Exception{
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            }catch (Exception e){
                System.out.println(e);
            }
            System.out.println("线程2加锁结果："+redisWithReentrantLock.lock("relockkey", "relockvalue", 20, TimeUnit.SECONDS));
        }).start();

        for (int i = 0; i <5 ; i++) {
            boolean lock = redisWithReentrantLock.lock("relockkey", "relockvalue", 20, TimeUnit.SECONDS);
            System.out.println("加锁"+lock+"后count:"+redisWithReentrantLock.getcount("relockkey"));
            TimeUnit.SECONDS.sleep(1);
        }
        while(true){
            boolean unlock = redisWithReentrantLock.unlock("relockkey", "relockvalue");
            if(redisWithReentrantLock.getcount("relockkey")<=0){
                System.out.println("全部解锁成功");
                //再试一次
//            System.out.println(redisWithReentrantLock.unlock("relockkey", "relockvalue"));
                return;
            }
            System.out.println("此次解锁："+unlock+"后："+redisWithReentrantLock.getcount("relockkey"));
        }

        //
    }
}
