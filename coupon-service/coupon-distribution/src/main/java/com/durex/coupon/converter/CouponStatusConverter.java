package com.durex.coupon.converter;

import com.durex.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * <h1>优惠券状态枚举属性转换器</h1>
 *
 * @author liugelong
 * @date 2021/10/20 10:16 下午
 */
@Converter
public class CouponStatusConverter implements
        AttributeConverter<CouponStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}

