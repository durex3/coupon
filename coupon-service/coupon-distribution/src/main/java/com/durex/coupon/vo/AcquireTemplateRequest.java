package com.durex.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>获取优惠券请求对象定义</h1>
 *
 * @author liugelong
 * @date 2021/10/21 10:48 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcquireTemplateRequest {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 优惠券模板信息
     */
    private CouponTemplateSDK templateSDK;
}
