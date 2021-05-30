package com.durex.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

/**
 * 优惠券模版启动类
 *
 * @author liugelong
 * @date 2021/4/24 5:36 下午
 */
@EnableJpaAuditing
@EnableFeignClients
@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
public class CouponDistributionApplication {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(CouponDistributionApplication.class, args);
    }
}
