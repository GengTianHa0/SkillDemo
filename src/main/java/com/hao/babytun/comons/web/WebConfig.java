package com.hao.babytun.comons.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Resource
    private AntiRefreashInterceptor antiRefreashInterceptor;


    //对商品页面进行拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(antiRefreashInterceptor).addPathPatterns(
                "/goods",
                "/gid/",
                "/abc/**");

    }
}
