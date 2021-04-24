package com.durex.coupon.constant;

import com.durex.coupon.exception.CouponException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券分类
 *
 * @author liugelong
 * @date 2021/4/24 11:25 下午
 */
@AllArgsConstructor
@Getter
public enum CouponCategory {

    MANJIAN("001", "满减券"),

    ZHEKOU("002", "折扣券"),

    LIJIAN("003", "立减券");

    /**
     * 优惠券分类编码
     */
    private final String code;

    /**
     * 优惠券分类描述
     */
    private final String description;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(CouponCategory.values())
                .filter((CouponCategory c) -> c.code.equals(code))
                .findAny()
                .orElseThrow(() -> new CouponException("coupon category " + code + " not exist"));
    }
}