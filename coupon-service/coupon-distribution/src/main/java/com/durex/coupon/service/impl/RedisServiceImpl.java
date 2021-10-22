package com.durex.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.durex.coupon.constant.Constant;
import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.entity.Coupon;
import com.durex.coupon.exception.CouponException;
import com.durex.coupon.service.IRedisService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liugelong
 * @date 2021/10/22 9:36 上午
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    /**
     * Redis 客户端
     */
    private final StringRedisTemplate redisTemplate;

    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Coupon> getCachedCouponList(Long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<Object> valueList = redisTemplate.opsForHash().values(redisKey);
        List<String> couponStrList = valueList.stream()
                .map(v -> Objects.toString(v, null))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(couponStrList)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Lists.newArrayList();
        }

        return couponStrList.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User: {}, Status: {}",
                userId, JSON.toJSONString(status));

        // key 是 coupon_id, value 是序列化的 Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        /*
          用户优惠券缓存信息
          KV
          K: status -> redisKey
          V: coupon_id: 序列化的 Coupon
         */
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        return null;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> couponList, Integer status) throws CouponException {
        return null;
    }

    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
            default:
                break;
        }
        return redisKey;
    }
}
