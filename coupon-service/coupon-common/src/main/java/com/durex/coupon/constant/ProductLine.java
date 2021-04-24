package com.durex.coupon.constant;

import com.durex.coupon.exception.CouponException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 产品线枚举
 *
 * @author liugelong
 * @date 2021/4/24 11:36 下午
 */
@AllArgsConstructor
@Getter
public enum ProductLine {

    DAMAO(1, "大猫"),

    DABAO(2, "大宝");

    /**
     * 产品线分类编码
     */
    private final Integer code;

    /**
     * 产品线描述
     */
    private final String description;

    public static ProductLine of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(ProductLine.values())
                .filter((ProductLine c) -> c.code.equals(code))
                .findAny()
                .orElseThrow(() -> new CouponException("product line " + code + " not exist"));
    }
}
