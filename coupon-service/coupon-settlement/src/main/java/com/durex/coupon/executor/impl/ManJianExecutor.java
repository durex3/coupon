package com.durex.coupon.executor.impl;

import com.durex.coupon.constant.RuleFlag;
import com.durex.coupon.executor.AbstractExecutor;
import com.durex.coupon.executor.RuleExecutor;
import com.durex.coupon.vo.CouponTemplateSDK;
import com.durex.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * <h1>满减优惠券结算规则执行器</h1>
 *
 * @author liugelong
 * @date 2021/11/17 4:23 下午
 */
@Slf4j
@Component
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {

    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * <h2>优惠券规则的计算</h2>
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsTotalPrice = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfoList())
        );

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsTotalPrice
        );
        if (null != probability) {
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfoList()
                .get(0).getTemplate();
        double base = templateSDK.getRule().getDiscount().getBase();
        double quota = templateSDK.getRule().getDiscount().getQuota();

        if (goodsTotalPrice < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settlement.setCost(goodsTotalPrice);
            settlement.setCouponAndTemplateInfoList(Collections.emptyList());
            return settlement;
        }

        double goodsFinalTotalPrice = Math.max(goodsTotalPrice - quota, minCost());

        // 计算使用优惠券之后的价格 - 结算
        settlement.setCost(retain2Decimals(
                goodsFinalTotalPrice
        ));
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}",
                goodsTotalPrice, settlement.getCost());

        return settlement;

    }
}
