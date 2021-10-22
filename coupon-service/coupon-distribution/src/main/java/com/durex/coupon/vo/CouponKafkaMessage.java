package com.durex.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>优惠券 kafka 消息对象定义</h1>
 *
 * @author liugelong
 * @date 2021/10/21 2:56 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {

    /**
     * 优惠券状态
     */
    private Integer status;

    /**
     * Coupon 主键
     */
    private List<Integer> idList;
}
