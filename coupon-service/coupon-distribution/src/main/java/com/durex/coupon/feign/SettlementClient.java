package com.durex.coupon.feign;

import com.durex.coupon.feign.hystrix.SettlementClientHystrix;
import com.durex.coupon.vo.CommonResponse;
import com.durex.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <h1>优惠券结算微服务 Feign 接口定义</h1>
 *
 * @author liugelong
 * @date 2021/10/28 9:52 下午
 */
@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    /**
     * <h2>优惠券规则计算</h2>
     */
    @PostMapping(value = "/coupon-settlement/settlement/compute")
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlement);
}
