package com.durex.coupon.service.impl;

import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.exception.CouponException;
import com.durex.coupon.repository.CouponTemplateRepository;
import com.durex.coupon.request.TemplateRequest;
import com.durex.coupon.service.IAsyncService;
import com.durex.coupon.service.IBuildTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 构建优惠券模版实现类
 *
 * @author liugelong
 * @date 2021/4/27 10:47 下午
 */
@Service
public class BuildTemplateService implements IBuildTemplateService {

    @Resource
    private CouponTemplateRepository templateRepository;
    @Resource
    private IAsyncService asyncService;

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) {
        if (templateRepository.findByName(request.getName()) != null) {
            throw new CouponException("exist same name template");
        }
        // 构造 CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template = templateRepository.save(template);

        // 根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /**
     * 将 TemplateRequest 转换为 CouponTemplate
     **/

    private CouponTemplate requestToTemplate(TemplateRequest request) {
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
