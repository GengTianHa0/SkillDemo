package com.hao.babytun.comons.web;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Component
public class AntiRefreashInterceptor implements HandlerInterceptor {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        //获取访问用户的Ip地址
        //return true开始接下来的服务
        String clientIP = request.getRemoteAddr();


        //针对与Ip相同 (例如机房) 我们需要再精确的判断一下"User-Agent" 即每个电脑都不同
        String userAgent= request.getHeader("User-Agent");
        String key=clientIP+"_"+userAgent;
        if (redisTemplate.hasKey("anti:refresh:blacklist")) {
            if (redisTemplate.opsForSet().isMember("anti:refresh:blacklist", clientIP)) {
                response.getWriter().println("检测到您的IP异常已被加入黑名单");
                return false;
            }
        }
        Integer num = (Integer) redisTemplate.opsForValue().get(key);
        if (num == null) {
            redisTemplate.opsForValue().set(key, 1l, 60, TimeUnit.SECONDS);
        } else {
            if (num > 30 && num < 100) {
                response.getWriter().println("请求过于频繁，请稍后再试");
                redisTemplate.opsForValue().increment(key, 1l);
                return false;
            } else if (num >= 100) {
                response.getWriter().println("检测到您的IP异常已被加入黑名单");
                redisTemplate.opsForSet().add("anti:refresh:blacklist", clientIP);
                return false;
            } else {
                redisTemplate.opsForValue().increment(key, 1l);
            }
        }
        return true;
    }
}
