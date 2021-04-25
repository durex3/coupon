package com.durex.coupon.constant;

import com.durex.coupon.exception.CouponException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 分发目标
 *
 * @author liugelong
 * @date 2021/4/24 11:44 下午
 */
@AllArgsConstructor
@Getter
public enum DistributeTarget {

    SINGLE(1, "单用户"),

    MULTI(2, "多用户");

    /**
     * 分发目标编码
     */
    private final Integer code;

    /**
     * 分发目标描述
     */
    private final String description;

    public static DistributeTarget of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(DistributeTarget.values())
                .filter((DistributeTarget c) -> c.code.equals(code))
                .findAny()
                .orElseThrow(() -> new CouponException("distribute target " + code + " not exist"));
    }

}
