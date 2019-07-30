package com.leo.cache.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class DruidConfig {
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource druidDarasource(){
//    return new DruidDataSource();
//}
////以下为配置druid的监控
//@Bean
//public ServletRegistrationBean druidServlet(){
//    ServletRegistrationBean servletRegistrationBean=new ServletRegistrationBean(new StatViewServlet(),
//            "/druid/*");
//    Map<String,String> initParams=new HashMap<>();
//    initParams.put("loginUsername","admin");
//    initParams.put("loginPassword","123456");
//    initParams.put("allow","");//默认就是允许所有访问
//    initParams.put("deny","192.168.15.21");
//    //是否能够重置数据 禁用HTML页面上的“Reset All”功能
//    initParams.put("resetEnable", "false");
//    servletRegistrationBean.setInitParameters(initParams);
//        return servletRegistrationBean;
//}
//@Bean
//public FilterRegistrationBean druidFilter(){
//    FilterRegistrationBean bean=new FilterRegistrationBean();
//    bean.setFilter(new WebStatFilter());
//
//    Map<String,String> initParams = new HashMap<>();
//    initParams.put("exclusions","*.js,*.css,/druid/*");
//    bean.setInitParameters(initParams);
//    bean.setUrlPatterns(Arrays.asList("/*"));
//    return  bean;
//}
}
