package com.durex.coupon.executor.impl;

import com.durex.coupon.constant.RuleFlag;
import com.durex.coupon.executor.AbstractExecutor;
import com.durex.coupon.executor.RuleExecutor;
import com.durex.coupon.vo.CouponTemplateSDK;
import com.durex.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>折扣优惠券结算规则执行器</h1>
 *
 * @author liugelong
 * @date 2021/11/25 5:46 下午
 */
@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsTotalPrice = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfoList())
        );

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsTotalPrice
        );
        if (null != probability) {
            log.debug("ZheKou Template Is Not Match GoodsType!");
            return probability;
        }

        // 折扣优惠券可以直接使用, 没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfoList()
                .get(0).getTemplate();
        double quota = templateSDK.getRule().getDiscount().getQuota();

        double goodsFinalTotalPrice = goodsTotalPrice * (quota / 100);

        settlement.setCost(
                retain2Decimals(
                        Math.max(goodsFinalTotalPrice, minCost())
                )
        );

        log.debug("Use ZheKou Coupon Make Goods Cost From {} To {}",
                goodsTotalPrice, settlement.getCost());

        return settlement;
    }
}
