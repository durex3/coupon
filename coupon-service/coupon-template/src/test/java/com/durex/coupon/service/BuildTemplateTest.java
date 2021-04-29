package com.durex.coupon.service;

import com.alibaba.fastjson.JSON;
import com.durex.coupon.constant.CouponCategory;
import com.durex.coupon.constant.DistributeTarget;
import com.durex.coupon.constant.PeriodType;
import com.durex.coupon.constant.ProductLine;
import com.durex.coupon.entity.CouponTemplate;
import com.durex.coupon.request.TemplateRequest;
import com.durex.coupon.vo.TemplateRule;
import com.durex.coupon.vo.TemplateRule.Discount;
import com.durex.coupon.vo.TemplateRule.Expiration;
import com.durex.coupon.vo.TemplateRule.Usage;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;

/**
 * 构造优惠券模版测试
 *
 * @author liugelong
 * @date 2021/4/29 10:38 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildTemplateTest {

    @Resource
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplateTest() {
        CouponTemplate couponTemplate = buildTemplateService.buildTemplate(mockTemplateRequest());
        System.out.println(JSON.toJSONString(couponTemplate));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TemplateRequest mockTemplateRequest() {
        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模版" + new Date().getTime());
        request.setLogo("http://www.durex3.com");
        request.setDesc("满减券");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10000);
        request.setUserId(10001L);
        request.setTarget(DistributeTarget.SINGLE.getCode());
        TemplateRule templateRule = new TemplateRule();

        Expiration expiration = new Expiration();
        expiration.setPeriod(PeriodType.SHIFT.getCode());
        expiration.setGap(1);
        expiration.setDeadline(DateUtils.addDays(new Date(), 60).getTime());
        templateRule.setExpiration(expiration);

        Discount discount = new Discount();
        discount.setQuota(20);
        discount.setBase(100);
        templateRule.setDiscount(discount);

        Usage usage = new Usage();
        usage.setProvince("广东省");
        usage.setCity("广州市");
        usage.setGoodsType(JSON.toJSONString(Lists.newArrayList("家具", "文娱")));
        templateRule.setUsage(usage);

        templateRule.setLimitation(1);
        templateRule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(templateRule);

        return request;
    }
}
