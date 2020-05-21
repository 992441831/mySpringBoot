package com.asiainfo.demo;

/**
 * Created by zhangqq20190306 on 2020/2/16.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// 必须添加注解，springmvc通过web.xml配置
//@Component
public class TimeFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(TimeFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("初始化过滤器：{}", filterConfig.getFilterName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LOG.info("start to doFilter");
        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long endTime = System.currentTimeMillis();
        LOG.info("the request of {} consumes {}ms.", getUrlFrom(request), (endTime - startTime));
        LOG.info("end to doFilter");
    }

    @Override
    public void destroy() {
        LOG.info("销毁过滤器");
    }

    private String getUrlFrom(ServletRequest servletRequest){
        if (servletRequest instanceof HttpServletRequest){
            return ((HttpServletRequest) servletRequest).getRequestURL().toString();
        }
        return "";
    }
}
