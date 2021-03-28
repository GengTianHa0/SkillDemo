package com.hao.babytun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

//mybatis在springboot启动的时候自动扫描mybatis实现的接口
@MapperScan("com.hao.babytun")
//利用注解来对缓存进行处理
@EnableCaching
//启动任务调度
@EnableScheduling
public class BabytunApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabytunApplication.class, args);
    }

}
