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
import org.apache.commons.lang3.RandomUtils;
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
import java.util.concurrent.TimeUnit;
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
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());

        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code: {}, {}, {}", templateId, redisKey, couponCode);

        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> couponList, Integer status) throws CouponException {
        log.info("Add Coupon To Cache: {}, {}, {}",
                userId, JSON.toJSONString(couponList), status);

        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, couponList);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, couponList);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, couponList);
                break;
        }

        return result;
    }

    /**
     * <h2>将新增的优惠券放入缓存</h2>
     *
     * @param userId     用户id
     * @param couponList 优惠券列表
     * @return 成功记录数 {@link Integer}
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> couponList) {
        // 如果 status 是 USABLE, 代表是新增加的优惠券
        // 只会影响一个 Cache: USER_COUPON_USABLE
        log.info("Add Coupon To Cache For Usable.");

        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);

        Map<String, String> needCachedObject = new HashMap<>();
        couponList.forEach(c ->
                needCachedObject.put(
                        c.getId().toString(),
                        JSON.toJSONString(c)
                )
        );

        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);

        log.info("Add {} Coupons To Cache: {}, {}",
                needCachedObject.size(), userId, redisKey);

        redisTemplate.expire(
                redisKey,
                getRandomExpirationTime(1, 2),
                TimeUnit.SECONDS
        );

        return needCachedObject.size();
    }

    /**
     * <h2>将已使用的优惠券放入缓存</h2>
     *
     * @param userId     用户id
     * @param couponList 优惠券列表
     * @return 成功记录数 {@link Integer}
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> couponList) {
        // 如果 status 是 USED, 代表用户操作是使用当前的优惠券, 影响到两个 Cache
        // USABLE, USED
        log.info("Add Coupon To Cache For Used.");

        Map<String, String> needCachedForUsed = new HashMap<>(couponList.size());


        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForUsed = status2RedisKey(
                CouponStatus.USED.getCode(), userId
        );

        // 获取当前用户可用的优惠券
        List<Coupon> curUsableCouponList = getCachedCouponList(
                userId, CouponStatus.USABLE.getCode()
        );
        // 当前可用的优惠券个数一定是大于1的
        assert curUsableCouponList.size() > couponList.size();

        couponList.forEach(c -> needCachedForUsed.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        // 校验当前的优惠券参数是否与 Cached 中的匹配
        List<Integer> curUsableIdList = curUsableCouponList.stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
        List<Integer> paramIdList = couponList.stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIdList, couponList)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIdList),
                    JSON.toJSONString(paramIdList));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }

        List<String> needCleanKey = paramIdList.stream()
                .map(Object::toString).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 1. 已使用的优惠券 Cache 缓存添加
                operations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);

                // 2. 可用的优惠券 Cache 需要清理
                operations.opsForHash().delete(redisKeyForUsable, needCleanKey);

                // 3. 重置过期时间
                operations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                operations.expire(
                        redisKeyForUsed,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(
                        redisTemplate.executePipelined(sessionCallback)
                )
        );

        return couponList.size();
    }

    /**
     * <h2>将已过期的优惠券放入缓存</h2>
     *
     * @param userId     用户id
     * @param couponList 优惠券列表
     * @return 成功记录数 {@link Integer}
     */
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> couponList) {
        // status 是 EXPIRED, 代表是已有的优惠券过期了, 影响到两个 Cache
        // USABLE, EXPIRED
        log.info("Add Coupon To Cache For Expired.");

        Map<String, String> needCachedForExpired = new HashMap<>(couponList.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );

        // 获取当前用户可用的优惠券
        List<Coupon> curUsableCouponList = getCachedCouponList(
                userId, CouponStatus.USABLE.getCode()
        );
        // 当前可用的优惠券个数一定是大于1的
        assert curUsableCouponList.size() > couponList.size();

        couponList.forEach(c -> needCachedForExpired.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        // 校验当前的优惠券参数是否与 Cached 中的匹配
        List<Integer> curUsableIdList = curUsableCouponList.stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
        List<Integer> paramIdList = couponList.stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIdList, couponList)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIdList),
                    JSON.toJSONString(paramIdList));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }

        List<String> needCleanKey = paramIdList.stream()
                .map(Object::toString).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 1. 已过期的优惠券 Cache needCachedForExpired
                operations.opsForHash().putAll(redisKeyForExpired, needCachedForExpired);

                // 2. 可用的优惠券 Cache 需要清理
                operations.opsForHash().delete(redisKeyForUsable, needCleanKey);

                operations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                operations.expire(
                        redisKeyForExpired,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(
                        redisTemplate.executePipelined(sessionCallback)
                )
        );

        return couponList.size();
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

    /**
     * <h2>获取一个随机的过期时间</h2>
     * 缓存雪崩: key 在同一时间失效
     *
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回 [min, max] 之间的随机秒数
     */
    private long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(
                min * 60L * 60L,
                max * 60L * 60L
        );
    }
}
