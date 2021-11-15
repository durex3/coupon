package com.durex.coupon.feign;

import com.durex.coupon.feign.hystrix.TemplateClientHystrix;
import com.durex.coupon.vo.CommonResponse;
import com.durex.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板微服务 Feign 接口定义</h1>
 *
 * @author liugelong
 * @date 2021/10/28 9:47 下午
 */
@FeignClient(value = "eureka-client-coupon-template", fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     */
    @GetMapping(value = "/coupon-template/template/sdk/usable")
    CommonResponse<List<CouponTemplateSDK>> getAllUsableTemplate();

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     */
    @GetMapping(value = "/coupon-template/template/sdk/mapping")
    CommonResponse<Map<Integer, CouponTemplateSDK>> getIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids
    );
}
