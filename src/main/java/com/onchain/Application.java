package com.onchain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Classname Application
 * @Description TODO
 * @Date 2020/6/30 18:29
 * @Created by zhaochen
 */
@Slf4j
@SpringBootApplication
@MapperScan(basePackages = {"com.onchain.mapper"})
@EnableTransactionManagement
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
