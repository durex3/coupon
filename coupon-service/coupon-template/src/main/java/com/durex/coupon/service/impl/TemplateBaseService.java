package com.durex.coupon.service.impl;

import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.exception.CouponException;
import com.durex.coupon.repository.CouponTemplateRepository;
import com.durex.coupon.service.ITemplateBaseService;
import com.durex.coupon.vo.CouponTemplateSDK;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠券模版基础服务类实现
 *
 * @author liugelong
 * @date 2021/4/27 10:58 下午
 */
@Service
public class TemplateBaseService implements ITemplateBaseService {

    @Resource
    private CouponTemplateRepository templateRepository;

    @Override
    public CouponTemplate getById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new CouponException("coupon template is not exist"));
    }

    @Override
    public List<CouponTemplateSDK> getAllUsableTemplate() {
        List<CouponTemplate> templateList = templateRepository.findAllByAvailableAndExpired(true, false);
        return templateList.stream().map(TemplateBaseService::couponTemplateToSDK)
                .collect(Collectors.toList());
    }

    /**
     * couponTemplate to CouponTemplateSDK
     *
     * @param template {@link CouponTemplate}
     * @return CouponTemplateSDK {@link CouponTemplateSDK}
     */
    private static CouponTemplateSDK couponTemplateToSDK(CouponTemplate template) {
        CouponTemplateSDK couponTemplateSDK = new CouponTemplateSDK();
        couponTemplateSDK.setId(template.getId());
        couponTemplateSDK.setName(template.getName());
        couponTemplateSDK.setLogo(template.getLogo());
        couponTemplateSDK.setDesc(template.getDesc());
        couponTemplateSDK.setCategory(template.getCategory().getCode());
        couponTemplateSDK.setProductLine(template.getProductLine().getCode());
        couponTemplateSDK.setCount(template.getCount());
        couponTemplateSDK.setUserId(template.getUserId());
        couponTemplateSDK.setKey(template.getKey());
        couponTemplateSDK.setTarget(template.getTarget().getCode());
        couponTemplateSDK.setRule(template.getRule());
        return couponTemplateSDK;
    }

    @Override
    public Map<Long, CouponTemplateSDK> getIds2TemplateSDK(Collection<Long> ids) {
        List<CouponTemplate> templateList = templateRepository.findAllById(ids);
        List<CouponTemplateSDK> templateSDKList = templateList.stream().map(TemplateBaseService::couponTemplateToSDK)
                .collect(Collectors.toList());
        return templateSDKList.stream()
                .collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }
}
