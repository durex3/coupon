package com.durex.coupon.dao;

import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Coupon Dao 接口定义</h1>
 *
 * @author liugelong
 * @date 2021/10/20 10:24 下午
 */
public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * <h2>根据 userId + 状态寻找优惠券记录</h2>
     * where userId = ... and status = ...
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
