package com.durex.coupon.service;

import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.entity.Coupon;
import com.durex.coupon.exception.CouponException;

import java.util.List;

/**
 * <h1>Redis 相关的操作服务接口定义</h1>
 * 1. 用户的三个状态优惠券 Cache 相关操作
 * 2. 优惠券模板生成的优惠券码 Cache 操作
 *
 * @author liugelong
 * @date 2021/10/20 10:29 下午
 */
public interface IRedisService {

    /**
     * <h2>根据 userId 和状态找到缓存的优惠券列表数据</h2>
     *
     * @param userId 用户 id
     * @param status 优惠券状态 {@link CouponStatus}
     * @return {@link List<Coupon>}, 注意, 可能会返回 null, 代表从没有过记录
     */
    List<Coupon> getCachedCouponList(Long userId, Integer status);

    /**
     * <h2>保存空的优惠券列表到缓存中</h2>
     *
     * @param userId 用户 id
     * @param status 优惠券状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * <h2>尝试从 Cache 中获取一个优惠券码</h2>
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * <h2>将优惠券保存到 Cache 中</h2>
     *
     * @param userId     用户 id
     * @param couponList {@link List<Coupon>}
     * @param status     优惠券状态
     * @return 保存成功的个数
     */
    Integer addCouponToCache(Long userId, List<Coupon> couponList, Integer status) throws CouponException;
}
