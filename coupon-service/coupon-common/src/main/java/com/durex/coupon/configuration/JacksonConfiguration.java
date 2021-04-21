package com.durex.coupon.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * jackson的自定义配置
 *
 * @author liugelong
 * @date 2021/4/21 10:34 下午
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss"
                )
        );
        return objectMapper;
    }
}
