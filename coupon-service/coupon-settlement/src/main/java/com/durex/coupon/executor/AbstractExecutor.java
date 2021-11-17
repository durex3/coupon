package com.durex.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.durex.coupon.vo.GoodsInfo;
import com.durex.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liugelong
 * @date 2021/11/17 4:09 下午
 */
public abstract class AbstractExecutor {

    /**
     * <h2>校验商品类型与优惠券是否匹配</h2>
     * 需要注意:
     * 1. 这里实现的单品类优惠券的校验, 多品类优惠券重载此方法
     * 2. 商品只需要有一个优惠券要求的商品类型去匹配就可以
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        // 获取商品类型
        List<Integer> goodsTypeList = settlement.getGoodsInfoList()
                .stream()
                .map(GoodsInfo::getType)
                .collect(Collectors.toList());

        // 获取优惠券模版支持的商品类型
        String templateGoodsTypeStr = settlement.getCouponAndTemplateInfoList()
                .get(0)
                .getTemplate()
                .getRule()
                .getUsage()
                .getGoodsType();
        List<Integer> templateGoodsTypeList = JSON.parseObject(
                templateGoodsTypeStr,
                new TypeReference<List<Integer>>() {
                }
        );

        // 存在交集即可
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsTypeList, templateGoodsTypeList)
        );
    }


    /**
     * <h2>处理商品类型与优惠券限制不匹配的情况</h2>
     *
     * @param settlement {@link SettlementInfo} 用户传递的结算信息
     * @param goodsSum   商品总价
     * @return {@link SettlementInfo} 已经修改过的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlement, double goodsSum) {

        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlement);

        // 当商品类型不满足时, 直接返回总价, 并清空优惠券
        if (!isGoodsTypeSatisfy) {
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfoList(Collections.emptyList());
            return settlement;
        }

        return null;
    }

    /**
     * <h2>商品总价</h2>
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {

        return goodsInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }

    /**
     * <h2>保留两位小数</h2>
     */
    protected double retain2Decimals(double value) {

        return BigDecimal.valueOf(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * <h2>最小支付费用</h2>
     */
    protected double minCost() {

        return 0.1f;
    }
}
