package com.durex.coupon.service.impl;

import com.durex.coupon.constant.Constant;
import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.repository.CouponTemplateRepository;
import com.durex.coupon.service.IAsyncService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步任务实现类
 *
 * @author liugelong
 * @date 2021/4/27 9:31 下午
 */
@Slf4j
@Service
public class AsyncService implements IAsyncService {

    @Resource
    private CouponTemplateRepository templateRepository;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Async(value = "getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        StopWatch watch = StopWatch.createStarted();
        Set<String> couponCodeSet = buildCouponCode(template);
        String redisKey = String.format(
                "%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,
                template.getId().toString()
        );
        log.info("push coupon code to redis: {}", stringRedisTemplate.opsForList().rightPushAll(redisKey, couponCodeSet));
        template.setAvailable(true);
        templateRepository.save(template);
        watch.stop();
        log.info("construct coupon code by template cost: {}ms", watch.getTime(TimeUnit.MICROSECONDS));
        log.info("coupon template({}) available", template.getId());
    }

    /**
     * 异步构建优惠券码
     * 优惠券码：对应每一张优惠券，18位
     * 前四位：产品线+类型
     * 中间六位：日期随机
     * 后八位：0-9随机数
     *
     * @param template {@link CouponTemplate}
     * @return {@link Set<String>}
     */
    private Set<String> buildCouponCode(CouponTemplate template) {
        StopWatch watch = StopWatch.createStarted();
        Set<String> result = Sets.newHashSetWithExpectedSize(template.getCount());
        // 前四位
        String prefix4 = template.getProductLine().getCode().toString() +
                template.getCategory().getCode();
        String date = new SimpleDateFormat("yyyyMMdd")
                .format(template.getCreateTime());
        for (int i = 0; i < template.getCount(); i++) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        watch.stop();
        log.info("build coupon code cost: {}ms", watch.getTime(TimeUnit.MICROSECONDS));
        return result;
    }

    /**
     * 构造优惠券码后14位
     *
     * @param date 优惠券的模版的创建时间
     * @return String {@link String}
     */
    private String buildCouponCodeSuffix14(String date) {
        List<Character> charList = date.chars().mapToObj((int c) -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(charList);
        // 中间六位
        String mid6 = charList.stream().map(Object::toString).collect(Collectors.joining());
        char[] bases = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        // 后八位
        String suffix8 = RandomStringUtils.random(1, bases) +
                RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
