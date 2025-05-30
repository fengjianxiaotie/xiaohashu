package com.quanxiaoha.xiaohashu.oss.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.quanxiaoha.xiaohashu")
public class XiaohashuOssBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaohashuOssBizApplication.class, args);
    }

}
