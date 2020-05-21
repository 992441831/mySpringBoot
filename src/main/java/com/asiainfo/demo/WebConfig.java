package com.asiainfo.demo;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqq20190306 on 2020/2/16.
 */
/*@Configuration*/
public class WebConfig {

    /**
     * 注册第三方过滤器
     * 功能与spring mvc中通过配置web.xml相同
     * @return
     */
    @Bean
    public FilterRegistrationBean thirdFilter(){
        TimeFilter thirdPartFilter = new TimeFilter();
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean() ;
        filterRegistrationBean.setFilter(thirdPartFilter);
        List<String > urls = new ArrayList<>();
        // 匹配所有请求路径
        urls.add("/*");
        filterRegistrationBean.setUrlPatterns(urls);
        return filterRegistrationBean;
    }
}
