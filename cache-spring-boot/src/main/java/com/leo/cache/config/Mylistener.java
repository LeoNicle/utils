package com.leo.cache.config;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Mylistener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("web init");
//        System.out.println(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("web destory");
    }
}
