package com.durex.coupon.feign.hystrix;

import com.durex.coupon.feign.SettlementClient;
import com.durex.coupon.vo.CommonResponse;
import com.durex.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liugelong
 * @date 2021/10/28 9:53 下午
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) {

        log.error("[eureka-client-coupon-settlement] computeRule" +
                "request error");

        settlement.setEmploy(false);
        settlement.setCost(-1.0);

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] request error",
                settlement
        );
    }
}
