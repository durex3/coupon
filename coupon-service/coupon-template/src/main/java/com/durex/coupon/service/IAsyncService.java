package com.durex.coupon.service;

import com.durex.coupon.entity.CouponTemplate;

/**
 * 异步任务服务
 *
 * @author liugelong
 * @date 2021/4/26 10:01 下午
 */
public interface IAsyncService {

    /**
     * 根据优惠券模版异步创建优惠券
     *
     * @param template {@link CouponTemplate}
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
