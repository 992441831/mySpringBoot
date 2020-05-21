package com.asiainfo.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by zhangqq20190306 on 2020/2/16.
 */
// java配置类
/*@Configuration*/
public class WebConfig2 implements WebMvcConfigurer {
    @Autowired
    private TimeInterceptor timeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(timeInterceptor);
    }
}
