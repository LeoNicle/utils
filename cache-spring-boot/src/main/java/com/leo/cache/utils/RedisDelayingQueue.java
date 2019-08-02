package com.leo.cache.utils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 延时队列
 * @param <T>
 */
public class RedisDelayingQueue<T> {
    static class TaskItem<T> {
        public String id;
        public T msg;
    }
    // fastjson 序列化对象中存在 generic 类型时，需要使用 TypeReference
    private Type TaskType = new TypeReference<TaskItem<T>>() { }.getType();
    private RedisTemplate redisTemplate;

    private String queueKey;
    private String host;
    private Integer port;

    public RedisDelayingQueue( String queueKey,RedisTemplate redisTemplate,String host,Integer port) {
        this.redisTemplate=redisTemplate;
        this.queueKey = queueKey;
        this.host = host;
        this.port = port;
    }
    /**
     * 加入延时队列
     */
    public void delay(T msg) {
        TaskItem task = new TaskItem();
        task.id = UUID.randomUUID().toString(); // 分配唯一的 uuid
        task.msg = msg;
        String s = JSON.toJSONString(task); // fastjson 序列化
        redisTemplate.opsForZSet().add(queueKey,s, System.currentTimeMillis() + 5000); // 塞入延时队列 ,5s 后再试
    }

    /**
     * 获取队列数数据并处理
     */
    public void loop() {
//        System.out.println(Thread.currentThread().isDaemon());
        while (!Thread.interrupted()) {
        // 只取一条
            try {
                Set values = redisTemplate.opsForZSet().rangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
                if (values.isEmpty()) {
                    try {
                        Thread.sleep(500); // 歇会继续
                    } catch (InterruptedException e) {
                        System.out.println("阻断？");
                        break;
                    }
                    continue;
                }
                String s = (String)values.iterator().next();
                if (redisTemplate.opsForZSet().remove(queueKey, s) > 0) { // 抢到了
                    TaskItem task = JSON.parseObject(s, TaskType); // fastjson 反序列化
                    this.handleMsg( (T)task.msg);
                }
            }catch (RedisConnectionFailureException e){
                System.out.println(Thread.currentThread().getName()+Thread.currentThread().isInterrupted());
                System.out.println("断开重连");
                //重连
                LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(host,port); // 注：这里使用的是Lettuce而非Jedis客户端
                connectionFactory.setValidateConnection(true);
                connectionFactory.afterPropertiesSet();//这个很关键
                connectionFactory.initConnection();
                redisTemplate.setConnectionFactory(connectionFactory);
            }

        }
    }

    //处理队列中拿出的数据
    public void handleMsg(T msg) {
        System.out.println(new Date().getTime() +Thread.currentThread().getName()+"处理数据:"+msg);
    }
//    public static void main(String[] args) {
//        Jedis jedis = new Jedis();
//        RedisDelayingQueue queue = new RedisDelayingQueue<>(jedis, "q-demo");
//        Thread producer = new Thread() {
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    queue.delay("codehole" + i);
//                }
//            }
//        };
//        Thread consumer = new Thread() {
//            public void run() {
//                queue.loop();
//            }
//        };
//        producer.start();
//        consumer.start();
//        try {
//            producer.join();
//            Thread.sleep(6000);
//            consumer.interrupt();
//            consumer.join();
//        }
//        catch (InterruptedException e) {
//        }
//    }
}
