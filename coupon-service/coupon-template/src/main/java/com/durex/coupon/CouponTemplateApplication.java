package com.durex.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 优惠券模版启动类
 *
 * @author liugelong
 * @date 2021/4/24 5:36 下午
 */
@EnableJpaAuditing
@EnableScheduling
@EnableEurekaClient
@SpringBootApplication
public class CouponTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponTemplateApplication.class, args);
    }
}
