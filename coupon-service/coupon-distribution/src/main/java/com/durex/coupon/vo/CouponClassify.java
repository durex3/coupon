package com.durex.coupon.vo;

import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.constant.PeriodType;
import com.durex.coupon.entity.Coupon;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * <h1>用户优惠券的分类, 根据优惠券状态</h1>
 *
 * @author liugelong
 * @date 2021/10/21 2:36 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    /**
     * 可以使用的
     */
    private List<Coupon> usable;

    /**
     * 已使用的
     */
    private List<Coupon> used;

    /**
     * 已过期的
     */
    private List<Coupon> expired;


    /**
     * <h2>对当前的优惠券进行分类</h2>
     */
    public static CouponClassify classify(List<Coupon> couponList) {

        List<Coupon> usable = Lists.newArrayListWithCapacity(couponList.size());
        List<Coupon> used = Lists.newArrayListWithCapacity(couponList.size());
        List<Coupon> expired = Lists.newArrayListWithCapacity(couponList.size());

        couponList.forEach(coupon -> {
            // 判断优惠券是否过期
            boolean isTimeExpire;
            long curTime = new Date().getTime();
            TemplateRule rule = coupon.getTemplateSDK().getRule();
            if (rule.getExpiration().getPeriod().equals(PeriodType.REGULAR.getCode())) {
                isTimeExpire = rule.getExpiration().getDeadline() <= curTime;
            } else {
                isTimeExpire = DateUtils.addDays(
                        coupon.getAssignTime(),
                        coupon.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }

            if (coupon.getStatus() == CouponStatus.USED) {
                used.add(coupon);
            } else if (coupon.getStatus() == CouponStatus.EXPIRED || isTimeExpire) {
                expired.add(coupon);
            } else {
                usable.add(coupon);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
