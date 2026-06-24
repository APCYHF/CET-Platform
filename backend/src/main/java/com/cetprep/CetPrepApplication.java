package com.cetprep;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 英语四六级服务平台启动类
 */
@SpringBootApplication
@MapperScan("com.cetprep.mapper")
@EnableAsync
public class CetPrepApplication {
    public static void main(String[] args) {
        SpringApplication.run(CetPrepApplication.class, args);
    }
}
