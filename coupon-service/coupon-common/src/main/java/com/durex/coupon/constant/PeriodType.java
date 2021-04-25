package com.durex.coupon.constant;

import com.durex.coupon.exception.CouponException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 有效期类型
 *
 * @author liugelong
 * @date 2021/4/25 9:08 下午
 */
@AllArgsConstructor
@Getter
public enum PeriodType {

    REGULAR(1, "固定型（固定日期）"),
    SHIFT(2, "变动型（以领取时间开始算）");

    /**
     * 有效期编码
     */
    private final Integer code;

    /**
     * 有效期描述
     */
    private final String description;

    public static PeriodType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(PeriodType.values())
                .filter((PeriodType c) -> c.code.equals(code))
                .findAny()
                .orElseThrow(() -> new CouponException("period type " + code + " not exist"));
    }
}
