package com.durex.coupon.service;

import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.request.TemplateRequest;

/**
 * 构建优惠券模板定义
 *
 * @author liugelong
 * @date 2021/4/26 9:28 下午
 */
public interface IBuildTemplateService {

    /**
     * 创建优惠券模板
     *
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link TemplateRequest} 模板信息实体
     */
    CouponTemplate buildTemplate(TemplateRequest request);
}
