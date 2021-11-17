package com.durex.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * <h1>优惠券结算微服务的启动入口</h1>
 *
 * @author liugelong
 * @date 2021/11/15 10:30 下午
 */
@EnableEurekaClient
@SpringBootApplication
public class CouponSettlementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponSettlementApplication.class, args);
    }

}
