package com.durex.coupon.service;

import com.durex.coupon.entity.Coupon;
import com.durex.coupon.exception.CouponException;
import com.durex.coupon.vo.AcquireTemplateRequest;
import com.durex.coupon.vo.CouponTemplateSDK;
import com.durex.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * <h1>用户服务相关的接口定义</h1>
 * 1. 用户三类状态优惠券信息展示服务
 * 2. 查看用户当前可以领取的优惠券模板 - coupon-template 微服务配合实现
 * 3. 用户领取优惠券服务
 * 4. 用户消费优惠券服务 - coupon-settlement 微服务配合实现
 *
 * @author liugelong
 * @date 2021/10/21 10:35 上午
 */
public interface IUserService {

    /**
     * <h2>根据用户 id 和状态查询优惠券记录</h2>
     *
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link List<Coupon>}
     */
    List<Coupon> getCouponListByStatus(Long userId, Integer status) throws CouponException;

    /**
     * <h2>根据用户 id 查找当前可以领取的优惠券模板</h2>
     *
     * @param userId 用户 id
     * @return {@link List<CouponTemplateSDK>}
     */
    List<CouponTemplateSDK> getAvailableTemplate(Long userId) throws CouponException;

    /**
     * <h2>用户领取优惠券</h2>
     *
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;


    /**
     * <h2>结算(核销)优惠券</h2>
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
