package com.leo.cache;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan(value="com.leo.cache.mapper")
public class CacheSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheSpringBootApplication.class, args);
    }

}
