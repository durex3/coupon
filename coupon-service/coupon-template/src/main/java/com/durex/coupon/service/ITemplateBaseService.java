package com.durex.coupon.service;

import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模版基础
 *
 * @author liugelong
 * @date 2021/4/26 10:24 下午
 */
public interface ITemplateBaseService {

    /**
     * 根据优惠券模版id 获取优惠券信息
     *
     * @param id 模版id
     * @return {@link CouponTemplate}
     */
    CouponTemplate getById(Long id);

    /**
     * 查询所有可用的优惠券模版
     *
     * @return {@link List<CouponTemplate>}
     */
    List<CouponTemplateSDK> getAllUsableTemplate();

    /**
     * 获取模版 idList 到 CouponTemplateSDK 的映射
     *
     * @param idCollection {@link Collection<Long>} id集合
     * @return {@link CouponTemplateSDK}
     */
    Map<Long, CouponTemplateSDK> getIdList2TemplateSDK(Collection<Long> idCollection);
}
