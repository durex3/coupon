package com.durex.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.durex.coupon.constant.CouponCategory;
import com.durex.coupon.constant.RuleFlag;
import com.durex.coupon.executor.AbstractExecutor;
import com.durex.coupon.executor.RuleExecutor;
import com.durex.coupon.vo.GoodsInfo;
import com.durex.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>满减 + 折扣优惠券结算规则执行器</h1>
 *
 * @author liugelong
 * @date 2021/11/25 5:17 下午
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * <h2>校验商品类型与优惠券是否匹配</h2>
     * 需要注意:
     * 1. 这里实现的满减 + 折扣优惠券的校验
     * 2. 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
     *
     * @param settlement {@link SettlementInfo} 用户传递的计算信息
     */
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        // 获取商品类型
        List<Integer> goodsTypeList = settlement.getGoodsInfoList()
                .stream()
                .map(GoodsInfo::getType)
                .collect(Collectors.toList());

        List<Integer> templateGoodsTypeList = new ArrayList<>();

        settlement.getCouponAndTemplateInfoList().forEach(ct -> {

            String goodsTypeStr = ct.getTemplate()
                    .getRule()
                    .getUsage()
                    .getGoodsType();

            List<Integer> typeList = JSON.parseObject(goodsTypeStr, new TypeReference<List<Integer>>() {
            });
            templateGoodsTypeList.addAll(typeList);
        });

        // 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsTypeList, templateGoodsTypeList
        ));
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
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct :
                settlement.getCouponAndTemplateInfoList()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        // 当前的折扣优惠券和满减券如果不能共用(一起使用), 清空优惠券, 返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsTotalPrice);
            settlement.setCouponAndTemplateInfoList(Collections.emptyList());
            return settlement;
        }

        List<SettlementInfo.CouponAndTemplateInfo> ctInfoList = new ArrayList<>();
        double manJianBase = manJian.getTemplate().getRule()
                .getDiscount().getBase();
        double manJianQuota = manJian.getTemplate().getRule()
                .getDiscount().getQuota();
        double goodsFinalTotalPrice = goodsTotalPrice;

        // 先计算满减
        if (goodsFinalTotalPrice >= manJianBase) {
            goodsFinalTotalPrice -= manJianQuota;
            ctInfoList.add(manJian);
        }

        // 再计算折扣
        double zheKouQuota = zheKou.getTemplate().getRule()
                .getDiscount().getQuota();
        goodsFinalTotalPrice *= zheKouQuota / 100;
        ctInfoList.add(zheKou);

        settlement.setCouponAndTemplateInfoList(ctInfoList);
        settlement.setCost(
                retain2Decimals(
                        Math.max(goodsFinalTotalPrice, minCost())
                )
        );
        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsTotalPrice, settlement.getCost());

        return settlement;
    }

    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manJian,
                                        SettlementInfo.CouponAndTemplateInfo zheKou) {

        if (manJian == null || zheKou == null) {
            return false;
        }

        String manjianKey = manJian.getTemplate().getKey()
                + String.format("%04d", manJian.getTemplate().getId());
        String zhekouKey = zheKou.getTemplate().getKey()
                + String.format("%04d", zheKou.getTemplate().getId());

        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manjianKey);
        String manJianWeightStr = manJian.getTemplate()
                .getRule()
                .getWeight();
        List<String> manJianWeightList = JSON.parseObject(manJianWeightStr, new TypeReference<List<String>>() {
        });
        allSharedKeysForManjian.addAll(manJianWeightList);

        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zhekouKey);
        String zheKouWeightStr = zheKou.getTemplate()
                .getRule()
                .getWeight();
        List<String> zheKounWeightList = JSON.parseObject(zheKouWeightStr, new TypeReference<List<String>>() {
        });
        allSharedKeysForZhekou.addAll(zheKounWeightList);

        return CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey),
                allSharedKeysForManjian
        ) || CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey),
                allSharedKeysForZhekou
        );
    }
}
