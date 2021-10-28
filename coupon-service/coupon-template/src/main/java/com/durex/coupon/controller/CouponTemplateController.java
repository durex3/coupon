package com.durex.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.request.TemplateRequest;
import com.durex.coupon.service.IBuildTemplateService;
import com.durex.coupon.service.ITemplateBaseService;
import com.durex.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模版接口
 *
 * @author liugelong
 * @date 2021/4/29 9:07 下午
 */
@RequestMapping(value = "/template")
@Slf4j
@RestController
public class CouponTemplateController {

    @Resource
    private IBuildTemplateService buildTemplateService;
    @Resource
    private ITemplateBaseService templateBaseService;

    /**
     * 构建优惠券模版
     *
     * @param request {@link TemplateRequest}
     * @return {@link CouponTemplate}
     */
    @PostMapping
    public CouponTemplate buildCouponTemplate(@RequestBody TemplateRequest request) {
        log.info("build coupon template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * 根据id获取优惠券模版
     *
     * @param id {@link Long}
     * @return {@link CouponTemplate}
     */
    @GetMapping(value = "/{id}")
    public CouponTemplate getTemplateInfo(@PathVariable(value = "id") Integer id) {
        log.info("get coupon template: {}", id);
        return templateBaseService.getById(id);
    }

    /**
     * 获取所有可用的优惠券模版
     *
     * @return {@link List<CouponTemplateSDK>}
     */
    @GetMapping(value = "/sdk/usable")
    public List<CouponTemplateSDK> getAllUsableTemplate() {
        log.info("get all usable coupon template");
        return templateBaseService.getAllUsableTemplate();
    }

    /**
     * 获取模版 ids 到 CouponTemplateSDK 的映射
     *
     * @param ids {@link Collection<CouponTemplateSDK>}
     * @return {@link CouponTemplateSDK>}
     */
    @GetMapping(value = "/sdk/mapping")
    public Map<Integer, CouponTemplateSDK> getIds2TemplateSDK(Collection<Integer> ids) {
        log.info("get get ids 2 coupon template: {}", JSON.toJSONString(ids));
        return templateBaseService.getIds2TemplateSDK(ids);
    }
}
