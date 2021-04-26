package com.durex.coupon.repository;

import com.durex.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 优惠券模版repository
 *
 * @author liugelong
 * @date 2021/4/25 11:42 下午
 */
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {

    /**
     * 根据模板名查询模板
     *
     * @param name 模板名
     * @return CouponTemplate
     */
    CouponTemplate findByName(@Param(value = "name") String name);

    /**
     * 根据 available 和 expired 查询记录
     *
     * @param available available
     * @param expired   expired
     * @return List<CouponTemplate>
     */
    List<CouponTemplate> findAllByAvailableAndExpired(
            @Param(value = "available") boolean available,
            @Param(value = "expired") boolean expired
    );

    /**
     * 根据 expired 查询记录
     *
     * @param expired expired
     * @return List<CouponTemplate>
     */
    List<CouponTemplate> findAllByExpired(
            @Param(value = "expired") boolean expired
    );
}
