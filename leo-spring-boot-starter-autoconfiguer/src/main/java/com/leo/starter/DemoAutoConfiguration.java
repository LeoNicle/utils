package com.leo.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(DemoProperties.class)
public class DemoAutoConfiguration {
    @Autowired
    DemoProperties demoProperties;

    @Bean
    public DemoService getDemoService(){
        return new DemoService().setDemoProperties(demoProperties);
    }
}
