package com.durex.coupon.entity;

import com.durex.coupon.constant.CouponStatus;
import com.durex.coupon.converter.CouponStatusConverter;
import com.durex.coupon.serialization.CouponSerialize;
import com.durex.coupon.vo.CouponTemplateSDK;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * <h1>优惠券(用户领取的优惠券记录)实体表</h1>
 *
 * @author liugelong
 * @date 2021/4/27 10:47 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 关联优惠券模板的主键(逻辑外键)
     */
    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    /**
     * 领取用户
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 优惠券码
     */
    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    /**
     * 领取时间
     */
    @CreatedDate
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;

    /**
     * 优惠券状态
     */
    @Column(name = "status", nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;

    /**
     * 用户优惠券对应的模板信息
     */
    @Transient
    private CouponTemplateSDK templateSDK;

    /**
     * <h2>返回一个无效的 Coupon 对象</h2>
     */
    public static Coupon invalidCoupon() {

        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    /**
     * <h2>构造优惠券</h2>
     */
    public Coupon(Integer templateId, Long userId, String couponCode,
                  CouponStatus status) {

        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }
}
