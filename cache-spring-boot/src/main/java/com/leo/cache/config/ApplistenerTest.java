package com.leo.cache.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextListener;

//@Component
public class ApplistenerTest implements ApplicationListener<ApplicationReadyEvent> {//一般的spring应用用ContextRefreshedEvent

    @Override
    public void onApplicationEvent(ApplicationReadyEvent contextRefreshedEvent) {
        System.out.println(contextRefreshedEvent.getApplicationContext().getBeanFactory().getBeanDefinitionCount());
    }
}
