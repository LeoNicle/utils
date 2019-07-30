package com.leo.cache.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Myconfig {

    @Bean
    public ServletListenerRegistrationBean getContextListener(){
        ServletListenerRegistrationBean<Mylistener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new Mylistener());
        return bean;
    }
}
