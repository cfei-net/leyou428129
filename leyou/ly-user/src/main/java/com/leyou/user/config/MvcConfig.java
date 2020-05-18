package com.leyou.user.config;

import com.leyou.user.interceptor.AppTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置拦截器
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private AppTokenInterceptor appTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appTokenInterceptor);
    }
}
