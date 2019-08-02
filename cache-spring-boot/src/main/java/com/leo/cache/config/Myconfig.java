package com.leo.cache.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties")
@Data
public class Myconfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Bean
    public ServletListenerRegistrationBean getContextListener(){
        ServletListenerRegistrationBean<Mylistener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new Mylistener());
        return bean;
    }
}
